package com.github.jlcool.flutterbuildpublish.ui;

import com.intellij.openapi.ui.DialogWrapper;
import javax.swing.*;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.intellij.ide.util.PropertiesComponent;

public class MyDialog extends DialogWrapper {
    private JPanel contentPane;
    private JLabel apkLabel;
    private ButtonGroup radioGroup;
    private JRadioButton radioButtonAPK;
    private JRadioButton radioButtonIOS;
    private JCheckBox checkBoxUpload;
    private JTextField apiKeyField; // 新增的文本框
    public String apiKey;
    public MyDialog() {
        super(true); // 设置为模态对话框
        init();
        setTitle("\u6253\u5305\u53D1\u5E03");

    }

    @Override
    protected JComponent createCenterPanel() {
        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        pack();
        JPanel apiKeyPanel = new JPanel();
        radioGroup = new ButtonGroup();
        apkLabel = new JLabel("\u6253\u5305\u7c7b\u578b");
        // 创建单选项
        radioButtonAPK = new JRadioButton("APK");
        radioButtonAPK.setActionCommand("apk");
        radioButtonIOS = new JRadioButton("IOS");
        radioButtonIOS.setActionCommand("ios");
        radioGroup.add(radioButtonAPK);
        radioGroup.add(radioButtonIOS);
        // 创建多选项
        checkBoxUpload = new JCheckBox("\u662f\u5426\u4e0a\u4f20");
        checkBoxUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 根据 checkBoxUpload 的选中状态设置 apiKeyPanel 的可见性
                apiKeyPanel.setVisible(checkBoxUpload.isSelected());
            }
        });

        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.X_AXIS));
        radioButtonPanel.add(Box.createHorizontalStrut(10)); // 添加一些间距
        radioButtonPanel.add(apkLabel);
        radioButtonPanel.add(Box.createRigidArea(new Dimension(5, 0))); // 添加间距
        radioButtonAPK.setSelected(PropertiesComponent.getInstance().getBoolean("radio_apk", false));
        radioButtonAPK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PropertiesComponent.getInstance().setValue("radio_apk", radioButtonAPK.isSelected());
            }
        });
        radioButtonPanel.add(radioButtonAPK);
        radioButtonPanel.add(Box.createHorizontalStrut(10)); // 添加间距
        radioButtonPanel.add(Box.createRigidArea(new Dimension(5, 0))); // 添加间距
        radioButtonIOS.setSelected(PropertiesComponent.getInstance().getBoolean("radio_ios", false));
        radioButtonIOS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PropertiesComponent.getInstance().setValue("radio_ios", radioButtonIOS.isSelected());
            }
        });
        radioButtonPanel.add(radioButtonIOS);
        radioButtonPanel.add(Box.createHorizontalGlue());

        JPanel checkboxButtonPanel = new JPanel();
        checkboxButtonPanel.setLayout(new BoxLayout(checkboxButtonPanel, BoxLayout.X_AXIS));
        checkboxButtonPanel.add(Box.createHorizontalStrut(10)); // 添加一些间距
        checkBoxUpload.setSelected(PropertiesComponent.getInstance().getBoolean("check_box_upload", false));
        checkBoxUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PropertiesComponent.getInstance().setValue("check_box_upload", checkBoxUpload.isSelected());
            }
        });
        checkboxButtonPanel.add(checkBoxUpload);
        checkboxButtonPanel.add(Box.createHorizontalGlue());


        apiKeyPanel.setLayout(new BoxLayout(apiKeyPanel, BoxLayout.X_AXIS));
        checkboxButtonPanel.add(Box.createHorizontalStrut(10));
        apiKeyPanel.add(new JLabel("Apikey:"));
        String lastApiKey = PropertiesComponent.getInstance().getValue("_api_key", "");
        apiKeyField= new JTextField();
        apiKeyField.setText(lastApiKey);
        apiKeyPanel.add(apiKeyField);


        // 将所有组件添加到内容面板
        contentPane.add(radioButtonPanel);
        contentPane.add(checkboxButtonPanel);
        contentPane.add(apiKeyPanel);
        apiKeyPanel.setVisible(checkBoxUpload.isSelected());
        contentPane.setPreferredSize(new Dimension(400, 100));
        return contentPane;
    }
    // 获取选中的单选项
    public String getSelectedRadio() {
        ButtonModel selected = radioGroup.getSelection();
        return selected.getActionCommand();
    }
    @Override
    protected void doOKAction() {
        ButtonModel selected = radioGroup.getSelection();
        apiKey = apiKeyField.getText();
        PropertiesComponent.getInstance().setValue("_api_key", apiKey);
        if (selected == null) {
            JOptionPane.showMessageDialog(null, "\u9009\u62e9\u4e00\u4e2a\u9700\u8981\u6253\u5305\u7684\u7c7b\u578b", "\u9519\u8bef", JOptionPane.ERROR_MESSAGE);
            return;
        }
        super.doOKAction();
    }
    // 获取多选项是否被选中
    public boolean isCheckBoxSelected() {
        return checkBoxUpload.isSelected();
    }
}