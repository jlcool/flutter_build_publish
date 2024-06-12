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
    private JRadioButton radioButtonNoBuild;
    private JRadioButton radioButtonAPK;
    private JRadioButton radioButtonIOS;
    private JRadioButton radioButtonWindows;
    private JCheckBox checkBoxUpload;
    private JCheckBox checkBoxDingDing;
    private JTextField apiKeyField;
    private JTextField dingdingTokenField;
    private JTextField atWhoField;
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

        JPanel apiKeyPanel = new JPanel();
        JPanel dingdingPanel = new JPanel();
        JPanel dingdingAtPanel = new JPanel();
        JPanel dingdingTokenPanel = new JPanel();
        radioGroup = new ButtonGroup();
        apkLabel = new JLabel("\u6253\u5305\u7c7b\u578b");
        // 创建单选项
        radioButtonNoBuild=new JRadioButton("不编译");
        radioButtonNoBuild.setActionCommand("nobuild");
        radioButtonAPK = new JRadioButton("APK");
        radioButtonAPK.setActionCommand("apk");
        radioButtonIOS = new JRadioButton("IOS");
        radioButtonIOS.setActionCommand("ios");
        radioButtonWindows = new JRadioButton("windows");
        radioButtonWindows.setActionCommand("windows");
        radioGroup.add(radioButtonNoBuild);
        radioGroup.add(radioButtonAPK);
        radioGroup.add(radioButtonIOS);
        radioGroup.add(radioButtonWindows);
        // 创建多选项
        checkBoxUpload = new JCheckBox("\u662f\u5426\u4e0a\u4f20");
        // 设置文本对齐方式为左边
        checkBoxUpload.setHorizontalTextPosition(SwingConstants.LEFT);
        checkBoxUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 根据 checkBoxUpload 的选中状态设置 apiKeyPanel 的可见性
                apiKeyPanel.setVisible(checkBoxUpload.isSelected());
            }
        });
        //是否发送钉钉消息
        checkBoxDingDing = new JCheckBox("\u662f\u5426\u9489\u9489");
        checkBoxDingDing.setHorizontalTextPosition(SwingConstants.LEFT);
        checkBoxDingDing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dingdingPanel.setVisible(checkBoxDingDing.isSelected());
            }
        });

        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.X_AXIS));
        radioButtonPanel.add(Box.createHorizontalStrut(10)); // 添加一些间距
        radioButtonPanel.add(apkLabel);
        radioButtonPanel.add(Box.createRigidArea(new Dimension(5, 0))); // 添加间距


        radioButtonNoBuild.setSelected(PropertiesComponent.getInstance().getInt("radio_button", 0)==0);
        radioButtonNoBuild.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PropertiesComponent.getInstance().setValue("radio_button", 0,0);
            }
        });
        radioButtonPanel.add(radioButtonNoBuild);


        radioButtonPanel.add(Box.createHorizontalStrut(10)); // 添加间距
        radioButtonPanel.add(Box.createRigidArea(new Dimension(5, 0))); // 添加间距
        radioButtonAPK.setSelected(PropertiesComponent.getInstance().getInt("radio_button", 0)==1);
        radioButtonAPK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PropertiesComponent.getInstance().setValue("radio_button", 1,0);
            }
        });
        radioButtonPanel.add(radioButtonAPK);


        radioButtonPanel.add(Box.createHorizontalStrut(10)); // 添加间距
        radioButtonPanel.add(Box.createRigidArea(new Dimension(5, 0))); // 添加间距
        radioButtonIOS.setSelected(PropertiesComponent.getInstance().getInt("radio_button", 0)==2);
        radioButtonIOS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PropertiesComponent.getInstance().setValue("radio_button", 2,0);
            }
        });
        radioButtonPanel.add(radioButtonIOS);
        radioButtonPanel.add(Box.createHorizontalStrut(10)); // 添加间距
        radioButtonPanel.add(Box.createRigidArea(new Dimension(5, 0))); // 添加间距

        radioButtonWindows.setSelected(PropertiesComponent.getInstance().getInt("radio_button", 0)==3);
        radioButtonWindows.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PropertiesComponent.getInstance().setValue("radio_button", 3,0);
            }
        });
        radioButtonPanel.add(radioButtonWindows);
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
        apiKeyPanel.add(Box.createHorizontalStrut(10));
        apiKeyPanel.add(new JLabel("Apikey:"));
        String lastApiKey = PropertiesComponent.getInstance().getValue("_api_key", "");
        apiKeyField= new JTextField();
        apiKeyField.setText(lastApiKey);
        apiKeyPanel.add(apiKeyField);

        JPanel dingdingButtonPanel = new JPanel();
        dingdingButtonPanel.setLayout(new BoxLayout(dingdingButtonPanel, BoxLayout.X_AXIS));
        dingdingButtonPanel.add(Box.createHorizontalStrut(10)); // 添加一些间距
        checkBoxDingDing.setSelected(PropertiesComponent.getInstance().getBoolean("check_box_ding_ding", false));
        checkBoxDingDing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PropertiesComponent.getInstance().setValue("check_box_ding_ding", checkBoxDingDing.isSelected());
            }
        });
        dingdingButtonPanel.add(checkBoxDingDing);
        dingdingButtonPanel.add(Box.createHorizontalGlue());

        dingdingPanel.setLayout(new BoxLayout(dingdingPanel, BoxLayout.Y_AXIS));

        dingdingTokenPanel.setLayout(new BoxLayout(dingdingTokenPanel, BoxLayout.X_AXIS));
        dingdingTokenPanel.add(Box.createHorizontalStrut(10));
        dingdingTokenPanel.add(new JLabel("Token:"));
        String dingToken = PropertiesComponent.getInstance().getValue("_ding_token", "");
        dingdingTokenField= new JTextField();
        dingdingTokenField.setText(dingToken);
        dingdingTokenPanel.add(dingdingTokenField);

//        dingdingAtPanel.setLayout(new BoxLayout(dingdingAtPanel, BoxLayout.X_AXIS));
//        dingdingAtPanel.add(Box.createHorizontalStrut(10));
//        dingdingAtPanel.add(new JLabel("@\u8c01:"));
//        String atwho = PropertiesComponent.getInstance().getValue("_at_who", "");
//        atWhoField= new JTextField();
//        atWhoField.setText(atwho);
//        dingdingAtPanel.add(atWhoField);

        dingdingPanel.add(dingdingTokenPanel);
//        dingdingPanel.add(dingdingAtPanel);


        // 将所有组件添加到内容面板
        contentPane.add(radioButtonPanel);
        contentPane.add(checkboxButtonPanel);
        contentPane.add(apiKeyPanel);
        contentPane.add(dingdingButtonPanel);
        contentPane.add(dingdingPanel);
        apiKeyPanel.setVisible(checkBoxUpload.isSelected());
        dingdingPanel.setVisible(checkBoxDingDing.isSelected());
        pack();
        return contentPane;
    }
    // 获取选中的单选项
    public String getSelectedRadio() {
        ButtonModel selected = radioGroup.getSelection();
        return selected.getActionCommand();
    }
    public String getDingToken() {
        return dingdingTokenField.getText();
    }
    public String getDingAt() {
        return atWhoField.getText();
    }
    @Override
    protected void doOKAction() {
        ButtonModel selected = radioGroup.getSelection();
        apiKey= apiKeyField.getText();
        PropertiesComponent.getInstance().setValue("_api_key",apiKey);
        PropertiesComponent.getInstance().setValue("_ding_token", dingdingTokenField.getText());
        //PropertiesComponent.getInstance().setValue("_at_who", atWhoField.getText());
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
    public boolean isWindowsSelected() {
        return radioButtonWindows.isSelected();
    }
}