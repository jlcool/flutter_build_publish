package com.github.jlcool.flutterbuildpublish.ui;

import com.intellij.openapi.ui.DialogWrapper;
import javax.swing.*;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyDialog extends DialogWrapper {
    private JPanel contentPane;
    private JLabel apkLabel;
    private ButtonGroup radioGroup;
    private JRadioButton radioButtonAPK;
    private JRadioButton radioButtonIOS;
    private JCheckBox checkBoxUpload;

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

        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.X_AXIS));
        radioButtonPanel.add(Box.createHorizontalStrut(10)); // 添加一些间距
        radioButtonPanel.add(apkLabel);
        radioButtonPanel.add(Box.createRigidArea(new Dimension(5, 0))); // 添加间距
        radioButtonPanel.add(radioButtonAPK);
        radioButtonPanel.add(Box.createHorizontalStrut(10)); // 添加间距
        radioButtonPanel.add(Box.createRigidArea(new Dimension(5, 0))); // 添加间距
        radioButtonPanel.add(radioButtonIOS);
        radioButtonPanel.add(Box.createHorizontalGlue());

        JPanel checkboxButtonPanel = new JPanel();
        checkboxButtonPanel.setLayout(new BoxLayout(checkboxButtonPanel, BoxLayout.X_AXIS));
        checkboxButtonPanel.add(Box.createHorizontalStrut(10)); // 添加一些间距
        checkboxButtonPanel.add(checkBoxUpload);
        checkboxButtonPanel.add(Box.createHorizontalGlue());
        // 将所有组件添加到内容面板
        contentPane.add(radioButtonPanel);
        contentPane.add(checkboxButtonPanel);
        contentPane.setPreferredSize(new Dimension(100, 100));
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