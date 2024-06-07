package com.github.jlcool.flutterbuildpublish.actions;

import static com.intellij.openapi.vcs.history.FileHistoryRefresher.findOrCreate;
import static io.flutter.actions.RunFlutterAction.getRunConfigSettings;

import com.github.jlcool.flutterbuildpublish.ui.MyDialog;
import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.content.ContentManagerAdapter;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.MessageView;
import com.intellij.util.keyFMap.KeyFMap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

import io.flutter.FlutterMessages;
import io.flutter.actions.FlutterBuildActionGroup;
import io.flutter.console.FlutterConsoles;
import io.flutter.pub.PubRoot;
import io.flutter.run.SdkFields;
import io.flutter.run.SdkRunConfig;
import io.flutter.sdk.FlutterSdk;
import io.flutter.utils.ProgressHelper;
import java.util.ArrayList;
import java.util.List;
public class MyToolbarButton extends AnAction {


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

//    @Override
//    public void actionPerformed(@NotNull final AnActionEvent event) {
//        Project project = event.getProject();
//        if (project == null) {
//            return;
//        }
//        final FlutterSdk sdk = FlutterSdk.getFlutterSdk(project);
//        if (sdk == null) {
//            return;
//        }
//        final RunnerAndConfigurationSettings settings = getRunConfigSettings(event);
//        if (settings == null) {
//            return;
//        }
//
//        final RunConfiguration configuration = settings.getConfiguration();
//        if (!(configuration instanceof SdkRunConfig)) {
//            // Action is disabled; shouldn't happen.
//            return;
//        }
//
//        final SdkRunConfig sdkRunConfig = (SdkRunConfig)configuration.clone();
//        final SdkFields fields = sdkRunConfig.getFields();
//        final String additionalArgs = fields.getAdditionalArgs();
//
//        String flavorArg = null;
//        final FlutterBuildActionGroup.BuildType buildType = FlutterBuildActionGroup.BuildType.APK;
//
//        if (fields.getBuildFlavor() != null) {
//            flavorArg = "--flavor=" + fields.getBuildFlavor();
//        }
//
//        final List<String> args = new ArrayList<>();
//        args.add(buildType.type);
//        if (additionalArgs != null) {
//            args.add(additionalArgs);
//        }
//        if (flavorArg != null) {
//            args.add(flavorArg);
//        }
//
//        final PubRoot pubRoot = PubRoot.forEventWithRefresh(event);
//
//
//        if (pubRoot != null) {
//
//
//        final ProgressHelper progressHelper = new ProgressHelper(project);
//        progressHelper.start("building");
//
//        ProcessAdapter processAdapter = new ProcessAdapter() {
//            @Override
//            public void processTerminated(@NotNull ProcessEvent event) {
//                progressHelper.done();
//                final int exitCode = event.getExitCode();
//                if (exitCode != 0) {
//                    FlutterMessages.showError("Error while building ", "`flutter build` returned: " + exitCode, project);
//                }
//            }
//        };
//
//        com.intellij.openapi.module.Module module = pubRoot.getModule(project);
//        if (module != null) {
//            //noinspection ConstantConditions
//            sdk.flutterBuild(pubRoot, args.toArray(new String[0])).startInModuleConsole(module, pubRoot::refresh, processAdapter);
//        }
//        else {
//            //noinspection ConstantConditions
//            final ColoredProcessHandler processHandler = sdk.flutterBuild(pubRoot, args.toArray(new String[0])).startInConsole(project);
//            if (processHandler == null) {
//                progressHelper.done();
//            }
//            else {
//                processHandler.addProcessListener(processAdapter);
//            }
//        }
//    }


//    @Override
//    public void actionPerformed(@NotNull final AnActionEvent event) {
//        Project project = event.getProject();
//        if (project == null) {
//            return;
//        }
//        RunManager runManager = RunManager.getInstance(project);
//        RunnerAndConfigurationSettings configurationSettings = runManager.getSelectedConfiguration();
//        if (configurationSettings == null) {
//            return;
//        }
//        RunConfiguration configuration = configurationSettings.getConfiguration();
//        if (configuration instanceof SdkRunConfig) {
//            SdkRunConfig sdkRunConfig = (SdkRunConfig) configuration;
//            SdkFields someField = sdkRunConfig.getFields();
//            String filePath = someField.getFilePath();
//            String flavor = someField.getBuildFlavor();
//            String additionalArgs = someField.getAdditionalArgs();
//            String attachArgs = someField.getAttachArgs();
//            Map<String, String> envs = someField.getEnvs();
//
//            System.out.println(someField);
//            // 准备命令列表，初始包含基础命令
//            List<String> commandList = new ArrayList<>();
//            commandList.add("D:\\fvm\\default\\bin\\flutter.bat");
//            commandList.add("build");
//            commandList.add("apk");
//            if (filePath != null && !filePath.isEmpty()) {
//                commandList.add(filePath);
//            }
//            commandList.add("--release");
//
//            // 检查flavor是否不为空或特定条件满足
//            if (flavor != null && !flavor.isEmpty()) {
//                // 添加flavor参数到命令列表
//                commandList.add("--flavor");
//                commandList.add(flavor);
//            }
//            if (additionalArgs != null && !additionalArgs.isEmpty()) {
//                commandList.add(additionalArgs);
//            }
//
//            try {
//                // 构建执行Flutter build命令的命令行
//
//
//                ProcessBuilder processBuilder = new ProcessBuilder(commandList);
//
//                // 设置工作目录为项目根目录
//                processBuilder.directory(new File(Objects.requireNonNull(project.getBasePath())));
//                Process process = processBuilder.start();
//
//                // 读取并打印进程输出，用于日志或错误处理
//                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    System.out.println(line); // 可以替换成你的日志记录方式
//                }
//                // 等待进程结束
//                int exitCode = process.waitFor();
//                if (exitCode == 0) {
//                    System.out.println("Flutter build completed successfully.");
//                } else {
//                    System.err.println("Flutter build failed with exit code: " + exitCode);
//                }
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                // 处理异常
//            }
//        }
//    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        MyDialog dialog = new MyDialog();

        // 显示对话框
        if (dialog.showAndGet()) {


            Project project = event.getProject();
            if (project == null) {
                return;
            }
            RunManager runManager = RunManager.getInstance(project);
            RunnerAndConfigurationSettings configurationSettings = runManager.getSelectedConfiguration();
            if (configurationSettings == null) {
                return;
            }
            RunConfiguration configuration = configurationSettings.getConfiguration();
            if (configuration instanceof SdkRunConfig) {
                SdkRunConfig sdkRunConfig = (SdkRunConfig) configuration;
                SdkFields someField = sdkRunConfig.getFields();
                String filePath = someField.getFilePath();
                String flavor = someField.getBuildFlavor();
                String additionalArgs = someField.getAdditionalArgs();
                String attachArgs = someField.getAttachArgs();
                Map<String, String> envs = someField.getEnvs();


                try {
                    GeneralCommandLine commandLine = new GeneralCommandLine();
                    commandLine.setExePath("D:\\fvm\\default\\bin\\flutter.bat");
                    commandLine.addParameter("build");
                    commandLine.addParameter(dialog.getSelectedRadio());

                    if (filePath != null && !filePath.isEmpty()) {
                        commandLine.addParameter(filePath);
                    }
                    commandLine.addParameter("--release");

                    // 检查flavor是否不为空或特定条件满足
                    if (flavor != null && !flavor.isEmpty()) {
                        // 添加flavor参数到命令列表
                        commandLine.addParameter("--flavor");
                        commandLine.addParameter(flavor);
                    }
                    if (additionalArgs != null && !additionalArgs.isEmpty()) {
                        commandLine.addParameter(additionalArgs);
                    }
                    commandLine.setWorkDirectory(project.getBasePath());

                    ColoredProcessHandler handler = new ColoredProcessHandler(commandLine);
                    final PubRoot pubRoot = PubRoot.forEventWithRefresh(event);
                    if (pubRoot != null) {
                        com.intellij.openapi.module.Module module = pubRoot.getModule(project);
                        if (module != null) {
                            FlutterConsoles.displayProcessLater(handler, module.getProject(), module, handler::startNotify);
                            MessageView messageView =MessageView.getInstance(event.getProject());
                            //如果编译窗口关闭则停止编译
                            messageView.getContentManager().addContentManagerListener(new ContentManagerAdapter() {
                                @Override
                                public void contentRemoved(ContentManagerEvent event) {
                                        handler.destroyProcess(); // 停止正在执行的命令
                                }
                            });

                            handler.addProcessListener(new ProcessAdapter() {
                                @Override
                                public void processTerminated(@NotNull ProcessEvent event) {
                                    int exitCode = event.getExitCode();
                                    if (exitCode == 0) {
                                        System.out.println("Flutter build completed successfully.");
                                    } else {
                                        System.err.println("Flutter build failed with exit code: " + exitCode);
                                    }
                                }
                            });

                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // 处理异常
                }
            }
        }
    }
    @Override
    public void update(@NotNull final AnActionEvent event) {
        boolean visibility = event.getProject() != null;
        event.getPresentation().setEnabled(visibility);
        event.getPresentation().setVisible(visibility);
    }

}
