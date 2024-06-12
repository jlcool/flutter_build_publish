package com.github.jlcool.flutterbuildpublish.actions;

import static com.intellij.openapi.vcs.history.FileHistoryRefresher.findOrCreate;
import static io.flutter.actions.FlutterBuildActionGroup.build;
import static io.flutter.actions.RunFlutterAction.getRunConfigSettings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jlcool.flutterbuildpublish.ProgressRequestBody;
import com.github.jlcool.flutterbuildpublish.dingding.RobotMessage;
import com.github.jlcool.flutterbuildpublish.ui.MyDialog;
import com.intellij.execution.ExecutionException;
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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.flutter.FlutterMessages;
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
import io.flutter.pub.PubRoots;
import java.nio.file.Paths;

public class MyToolbarButton extends AnAction {
    static  String basePat="";
static final String podsPath= Paths.get("Pods", "Target Support Files", "Pods-Runner").toString();
static final String workspacePath=Paths.get("ios",  "Runner.xcworkspace").toString();
    static final String archivePath=Paths.get("build",  "ios","iphoneos","Runner.xcarchive").toString();
    static final String ipaExportPath=Paths.get("build",  "ios","iphoneos","Runner").toString();
    public static void build(@NotNull Project project,
                             @NotNull PubRoot pubRoot,
                             @NotNull FlutterSdk sdk,
                             @NotNull MyDialog dialog,
                             String pathname,
                             @NotNull String... additionalArgs) {
        final ProgressHelper progressHelper = new ProgressHelper(project);
        progressHelper.start("打包中");
        final com.intellij.openapi.module.Module module = pubRoot.getModule(project);
        ProcessAdapter processAdapter = new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {

                final int exitCode = event.getExitCode();
                if (exitCode != 0) {
                    progressHelper.done();
                    FlutterMessages.showError("Error while building " + additionalArgs, "`flutter build` returned: " + exitCode, project);
                }else{
                    FlutterConsoles.displayMessage(project, module, "编译完成\n");
                    progressHelper.done();
                    if(dialog.isCheckBoxSelected()) {
                        uploadApk(pathname, project, module, dialog);

                    }else{
                        progressHelper.done();
                    }
                }
                progressHelper.cancel();
            }
        };

        if (module != null) {
            MessageView messageView = MessageView.getInstance(project);
            //如果编译窗口关闭则停止编译
            messageView.getContentManager().addContentManagerListener(new ContentManagerAdapter() {
                @Override
                public void contentRemoved(ContentManagerEvent event) {
                    progressHelper.cancel();
                }
            });

            //noinspection ConstantConditions
            sdk.flutterBuild(pubRoot, additionalArgs).startInModuleConsole(module, pubRoot::refresh, processAdapter);
        }
        else {
            //noinspection ConstantConditions
            final ColoredProcessHandler processHandler = sdk.flutterBuild(pubRoot, additionalArgs).startInConsole(project);
            if (processHandler == null) {
                progressHelper.done();
            }
            else {
                processHandler.addProcessListener(processAdapter);
            }
        }
    }


    public void podUpdate( ){
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath("pod");
        commandLine.addParameter("update");
        ColoredProcessHandler handler = null;
        try {
            handler = new ColoredProcessHandler(commandLine);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        handler.startNotify();
    }
    public void podInstall( ){
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath("pod");
        commandLine.addParameter("install");
        commandLine.addParameter("--verbose");
        ColoredProcessHandler handler = null;
        try {
            handler = new ColoredProcessHandler(commandLine);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        handler.startNotify();
    }
    public void clean( ){
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath("xcodebuild");
        commandLine.addParameter("clean");
        commandLine.addParameter("-workspace");
        commandLine.addParameter(workspacePath);
        commandLine.addParameter("-scheme");
        commandLine.addParameter("Runner");
        commandLine.addParameter("-configuration");
        commandLine.addParameter("release");
        ColoredProcessHandler handler = null;
        try {
            handler = new ColoredProcessHandler(commandLine);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        handler.startNotify();
    }
    public void xcodebuildArchive( ){
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath("xcodebuild");
        commandLine.addParameter("archive");
        commandLine.addParameter("-workspace");
        commandLine.addParameter(workspacePath);
        commandLine.addParameter("-scheme");
        commandLine.addParameter("Runner");
        commandLine.addParameter("-configuration");
        commandLine.addParameter("release");
        commandLine.addParameter("-archivePath");
        commandLine.addParameter(archivePath);
        ColoredProcessHandler handler = null;
        try {
            handler = new ColoredProcessHandler(commandLine);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        handler.startNotify();
    }
    public void xcodebuildExportArchive( ){
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath("xcodebuild");
        commandLine.addParameter("-exportArchive");
        commandLine.addParameter("-archivePath");
        commandLine.addParameter(archivePath);
        commandLine.addParameter("-exportPath");
        commandLine.addParameter(ipaExportPath);
        commandLine.addParameter("-exportOptionsPlist");
        commandLine.addParameter("release.plist");
        ColoredProcessHandler handler = null;
        try {
            handler = new ColoredProcessHandler(commandLine);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        handler.startNotify();
    }
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
            basePat=project.getBasePath();
            final FlutterSdk sdk = FlutterSdk.getFlutterSdk(project);
            if (sdk == null) {
                return;
            }

            RunManager runManager = RunManager.getInstance(project);
            RunnerAndConfigurationSettings configurationSettings = runManager.getSelectedConfiguration();
            if (configurationSettings == null) {
                return;
            }
            RunConfiguration configuration = configurationSettings.getConfiguration();
            if (configuration instanceof SdkRunConfig sdkRunConfig) {
                SdkFields someField = sdkRunConfig.getFields();
                String filePath = someField.getFilePath();
                String flavor = someField.getBuildFlavor();
                String additionalArgs = someField.getAdditionalArgs();
                try {
                    final List<String> args = new ArrayList<>();
                    args.add(dialog.getSelectedRadio());
                    if (filePath != null && !filePath.isEmpty()) {
                        args.add(filePath);
                    }
                    args.add("--release");

                    // 检查flavor是否不为空或特定条件满足
                    if (flavor != null && !flavor.isEmpty() && !dialog.isWindowsSelected()) {
                        // 添加flavor参数到命令列表
                        args.add("--flavor");
                        args.add(flavor);
                    }
                    if (additionalArgs != null && !additionalArgs.isEmpty()) {
                        args.add(additionalArgs);
                    }
                    String pathname=project.getBasePath() + "/build/app/outputs/flutter-apk/app" + (flavor != null && !flavor.isEmpty() ? "-" : "") + flavor + "-release.apk";
                    if(Objects.equals(dialog.getSelectedRadio(), "ios")){
                        pathname=project.getBasePath() + "/build/app/outputs/flutter-apk/app" + (flavor != null && !flavor.isEmpty() ? "-" : "") + flavor + "-release.apk";
                    }

                    final PubRoot pubRoot = PubRoot.forEventWithRefresh(event);

                    if (pubRoot != null) {
                        final com.intellij.openapi.module.Module module = pubRoot.getModule(project);
                        if(Objects.equals(dialog.getSelectedRadio(), "nobuild")){
                            if(dialog.isCheckBoxSelected()) {
                                uploadApk(pathname, project, module, dialog);
                            }
                        }else {
                            build(project, pubRoot, sdk, dialog, pathname, args.toArray(new String[0]));
                        }

                    }else{
                        List<PubRoot> roots = PubRoots.forProject(project);
                        for (PubRoot sub : roots) {
                            final com.intellij.openapi.module.Module module = sub.getModule(project);
                            if(Objects.equals(dialog.getSelectedRadio(), "nobuild")){
                                if(dialog.isCheckBoxSelected()) {
                                    uploadApk(pathname, project, module, dialog);
                                }
                            }else {
                                build(project, sub, sdk, dialog, pathname, args.toArray(new String[0]));
                            }
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
        e.getPresentation().setVisible(enable);
    }

    private static void uploadApk(String pathname, Project project, com.intellij.openapi.module.Module module, MyDialog dialog) {
        File apkFile = new File(pathname);
        if (apkFile.exists()) {
            OkHttpClient client = new OkHttpClient();
            ProgressRequestBody requestBody = new ProgressRequestBody(apkFile, new ProgressRequestBody.UploadCallback() {
                @Override
                public void onProgressUpdate(int percentage) {
                    FlutterConsoles.displayMessage(project, module, "\u4e0a\u4f20\u8fdb\u5ea6: " + percentage + "%\n");
                }
            });


            File changelogFile = new File(project.getBasePath() + "/CHANGELOG.md");
            StringBuilder changelogContent = new StringBuilder();
            String targetVersion = getVersionNameFromPubspecYaml(project, module); // 需要读取的版本号
            boolean isTargetVersion = false;
            if (changelogFile.exists()) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(changelogFile), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().equals(targetVersion)) {
                            isTargetVersion = true;
                            continue;
                        }
                        if (isTargetVersion) { // 如果已找到目标版本且当前行不是版本号
                            if (line.matches("\\d+\\.\\d+\\.\\d+")) {
                                break;
                            }
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
                        InputStream inputStream = response.body().byteStream();

                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(inputStream);
                        String buildName=jsonNode.get("data").get("buildName").asText();
                        String buildType = jsonNode.get("data").get("buildType").asInt()==1?"IOS": "Android";
                        String buildVersion=jsonNode.get("data").get("buildVersion").asText();
                        int buildBuildVersion=jsonNode.get("data").get("buildBuildVersion").asInt();
                        String buildUpdated=jsonNode.get("data").get("buildUpdated").asText();
                        int buildFileSize=jsonNode.get("data").get("buildFileSize").asInt();
                        String buildShortcutUrl=jsonNode.get("data").get("buildShortcutUrl").asText();
                        String buildUpdateDescription=jsonNode.get("data").get("buildUpdateDescription").asText();
                        String buildQRCodeURL=jsonNode.get("data").get("buildQRCodeURL").asText();
                        String buildIcon=jsonNode.get("data").get("buildIcon").asText();
                        try {
                            RobotMessage.sendMessage(dialog, false,"应用更新","**应用更新提醒**\n\n应用名称："+buildName+"\n\n" +
                                    "应用类型："+buildType+"\n\n" +
                                    "版本信息："+buildVersion+"(Build "+buildBuildVersion+")\n\n" +
                                    "应用大小："+String.format("%.2f",buildFileSize/1024./1024.0)+" MB\n\n" +
                                    "更新时间："+buildUpdated+"\n\n" +
                                    "更新内容："+buildUpdateDescription+"\n\n"+
                                    "![screenshot]("+buildQRCodeURL+") "+"\n\n"+
                                    "## [点击下载](https://www.pgyer.com/"+buildShortcutUrl+")");
                            FlutterConsoles.displayMessage(project, module, "\u4e0a\u4f20\u6210\u529f.\n");
                        } catch (Exception e) {
                            FlutterConsoles.displayMessage(project, module, e.getMessage());
                        }
                    } else {
                        FlutterConsoles.displayMessage(project, module, "\u4e0a\u4f20\u5931\u8d25: " + response.message() + "\n");
                    }
                }
            });
        } else {
            FlutterConsoles.displayMessage(project, module, "上传文件未找到\n");
        }
    }

    private static String getVersionNameFromPubspecYaml(Project project, com.intellij.openapi.module.Module module) {
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
