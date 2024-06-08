package com.github.jlcool.flutterbuildpublish.actions;

import static com.intellij.openapi.vcs.history.FileHistoryRefresher.findOrCreate;
import static io.flutter.actions.RunFlutterAction.getRunConfigSettings;

import com.github.jlcool.flutterbuildpublish.ProgressRequestBody;
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
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

import io.flutter.FlutterMessages;
import io.flutter.actions.FlutterBuildActionGroup;
import io.flutter.console.FlutterConsoles;
import io.flutter.pub.PubRoot;
import io.flutter.run.LaunchState;
import io.flutter.run.SdkFields;
import io.flutter.run.SdkRunConfig;
import io.flutter.sdk.FlutterSdk;
import io.flutter.utils.ProgressHelper;
import java.util.ArrayList;
import com.intellij.execution.ui.ConsoleViewContentType;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MyToolbarButton extends AnAction {


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
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
                    if (flavor != null && !flavor.isEmpty() && !dialog.isWindowsSelected()) {
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
                            MessageView messageView = MessageView.getInstance(event.getProject());
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
                                        FlutterConsoles.displayMessage(project, module, "编译完成\n");
                                        File apkFile = new File(project.getBasePath() + "/build/app/outputs/flutter-apk/app-"+flavor+"-release.apk");
                                        if (apkFile.exists() && dialog.isCheckBoxSelected()) {
                                            uploadApk(apkFile, project,module,dialog);
                                        } else {
                                            FlutterConsoles.displayMessage(project, module, "上传文件未找到\n");
                                        }
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
    public void update(@NotNull final AnActionEvent e) {
        var project=e.getProject();
        if (project ==null)
        {
            return;
        }
        RunManager runManager = RunManager.getInstance(project);
        RunnerAndConfigurationSettings configurationSettings = runManager.getSelectedConfiguration();
        if (configurationSettings == null) {
            return;
        }
        RunConfiguration configuration = configurationSettings.getConfiguration();
        var enable= configuration instanceof SdkRunConfig && LaunchState.getRunningAppProcess((SdkRunConfig)configuration) == null;
        e.getPresentation().setEnabled(enable);
    }

    private void uploadApk(File apkFile, Project project,com.intellij.openapi.module.Module module,MyDialog dialog) {
        OkHttpClient client = new OkHttpClient();
        ProgressRequestBody requestBody = new ProgressRequestBody(apkFile, new ProgressRequestBody.UploadCallback() {
            @Override
            public void onProgressUpdate(int percentage) {
                FlutterConsoles.displayMessage(project, module, "\u4e0a\u4f20\u8fdb\u5ea6: " + percentage + "%\n");
            }
        });


        File changelogFile = new File(project.getBasePath() + "/CHANGELOG.md");
        StringBuilder changelogContent = new StringBuilder();
        String targetVersion = getVersionNameFromPubspecYaml(project,module); // 需要读取的版本号
        boolean isTargetVersion = false;
        if (changelogFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(changelogFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equals(targetVersion)) {
                        isTargetVersion = true;
                        continue;
                    }
                    if (isTargetVersion && !line.matches("\\d+\\.\\d+\\.\\d+")) { // 如果已找到目标版本且当前行不是版本号
                        changelogContent.append(line).append("\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                FlutterConsoles.displayMessage(project, module, "读取 CHANGELOG.md 文件失败: " + e.getMessage() + "\n");
            }
        } else {
            FlutterConsoles.displayMessage(project, module, "CHANGELOG.md 文件不存在.\n");
        }

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", apkFile.getName(), requestBody)
                .addFormDataPart("_api_key", dialog.apiKey)
                .addFormDataPart("buildInstallType", "1")
                .addFormDataPart("buildUpdateDescription", changelogContent.toString())

                .build();
        Request request = new Request.Builder()
                .url("https://www.pgyer.com/apiv2/app/upload") // Replace with your server URL
                .post(multipartBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                FlutterConsoles.displayMessage(project, module, "\u4e0a\u4f20\u5931\u8d25: " + e.getMessage() + "\n");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    FlutterConsoles.displayMessage(project, module, "\u4e0a\u4f20\u6210\u529f.\n");
                } else {
                    FlutterConsoles.displayMessage(project, module, "\u4e0a\u4f20\u5931\u8d25: " + response.message() + "\n");
                }
            }
        });
    }
    private String getVersionNameFromPubspecYaml(Project project,com.intellij.openapi.module.Module module) {
        File pubspecFile = new File(project.getBasePath(), "pubspec.yaml");
        if (!pubspecFile.exists()) {
            FlutterConsoles.displayMessage(project, module, "pubspec.yaml 文件不存在.\n");
            return "无法获取版本号";
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(pubspecFile, Charset.forName("UTF-8")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("version: ")) {
                    String[] parts = line.split(":")[1].split("\\+");
                    return parts[0].trim();
                }
            }
        } catch (IOException e) {
            FlutterConsoles.displayMessage(project, module, "读取 pubspec.yaml 文件失败: " + e.getMessage() + "\n");
        }

        return "无法获取版本号";
    }
}
