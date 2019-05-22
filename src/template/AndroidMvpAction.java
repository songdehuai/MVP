package template;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;

/**
 * Created with IntelliJ IDEA
 * User songdehuai
 * Time 19/5/22.
 */
public class AndroidMvpAction extends AnAction {
    Project project;
    VirtualFile selectGroup;


    private String activityFileName = "Activity.java";
    private String fragmentFileName = "Fragment.java";
    private String presenterFilename = "Presenter.java";
    private String contractFileName = "Contract.java";

    private boolean isKotlin = true;

    InputValidator inputValidator = new InputValidator() {
        @Override
        public boolean checkInput(String s) {
            return !s.isEmpty();
        }

        @Override
        public boolean canClose(String s) {
            return true;
        }
    };

    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();
        String className = "";
        Pair<String, Boolean> pair = Messages.showInputDialogWithCheckBox("请输入Activity名", "MVP", "Kotlin", true, true, Messages.getInformationIcon(), "", inputValidator);
        className = pair.first;
        isKotlin = pair.second;
        if (isKotlin) {
            activityFileName = "Activity.kt";
            fragmentFileName = "Fragment.kt";
            presenterFilename = "Presenter.kt";
            contractFileName = "Contract.kt";
        } else {
            activityFileName = "Activity.java";
            fragmentFileName = "Fragment.java";
            presenterFilename = "Presenter.java";
            contractFileName = "Contract.java";
        }
        selectGroup = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (className == null || className.equals("")) {
            System.out.print("没有输入类名");
            return;
        }
        if (className.equalsIgnoreCase("mvp")) {
            createMvpBase();
        } else {
            createClassMvp(className);
        }
//        project.getBaseDir().refresh(false, true);
        project.getWorkspaceFile().refresh(false, true);
    }

    /**
     * 创建MVP的Base文件夹
     */
    private void createMvpBase() {
        String path = selectGroup.getPath() + "/mvp";
        String packageName = path.substring(path.indexOf("java") + 5, path.length()).replace("/", ".");

        String presenter = readFile("BasePresenter.txt").replace("&package&", packageName);
        String presenterImpl = readFile("BasePresenterImpl.txt").replace("&package&", packageName);
        String view = readFile("BaseView.txt").replace("&package&", packageName);
        String activity = readFile("MVPBaseActivity.txt").replace("&package&", packageName);
        String fragment = readFile("MVPBaseFragment.txt").replace("&package&", packageName);

        writetoFile(presenter, path, "BasePresenter.java");
        writetoFile(presenterImpl, path, "BasePresenterImpl.java");
        writetoFile(view, path, "BaseView.java");
        writetoFile(activity, path, "MVPBaseActivity.java");
        writetoFile(fragment, path, "MVPBaseFragment.java");

    }

    /**
     * 创建MVP架构
     */
    private void createClassMvp(String className) {
        boolean isFragment = className.endsWith("Fragment") || className.endsWith("fragment");
        if (className.endsWith("Fragment") || className.endsWith("fragment") || className.endsWith("Activity") || className.endsWith("activity")) {
            className = className.substring(0, className.length() - 8);
        }

        String path = selectGroup.getPath() + "/" + className.toLowerCase();

        String packageName = path.substring(path.indexOf("java") + 5, path.length()).replace("/", ".");
        String mvpPath = FileUtil.traverseFolder(path.substring(0, path.indexOf("java")));
        mvpPath = mvpPath.substring(mvpPath.indexOf("java") + 5, mvpPath.length()).replace("/", ".").replace("\\", ".");

        className = className.substring(0, 1).toUpperCase() + className.substring(1);

        System.out.print(mvpPath + "---" + className + "----" + packageName);

        //创建Contract
        String contract = readFile("Contract.txt").replace("&package&", packageName).replace("&mvp&", mvpPath).replace("&Contract&", className + "Contract");
        //创建Presenter
        String presenter = readFile("Presenter.txt").replace("&package&", packageName).replace("&mvp&", mvpPath).replace("&Contract&", className + "Contract").replace("&Presenter&", className + "Presenter");
        if (isFragment) {
            //创建Fragment
            String fragment = readFile("Fragment.txt").replace("&package&", packageName).replace("&mvp&", mvpPath).replace("&Fragment&", className + "Fragment").replace("&Contract&", className + "Contract").replace("&Presenter&", className + "Presenter");
            writetoFile(fragment, path + "/fragment", className + fragmentFileName);
        } else {
            //创建Activity
            String activity = readFile("Activity.txt").replace("&package&", packageName).replace("&mvp&", mvpPath).replace("&Activity&", className + "Activity").replace("&Contract&", className + "Contract").replace("&Presenter&", className + "Presenter");
            writetoFile(activity, path + "/activity", className + activityFileName);
        }
        writetoFile(contract, path + "/contract", className + contractFileName);
        writetoFile(presenter, path + "/presenter", className + presenterFilename);
    }


    private String readFile(String filename) {
        String name = "code/" + filename;
        if (isKotlin) {
            name = "kt/" + filename;
        }
        InputStream in = null;
        in = this.getClass().getResourceAsStream(name);
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (Exception e) {
        }
        return content;
    }

    private void writetoFile(String content, String filepath, String filename) {
        try {
            File floder = new File(filepath);
            // if file doesnt exists, then create it
            if (!floder.exists()) {
                floder.mkdirs();
            }
            File file = new File(filepath + "/" + filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }

        } catch (IOException e) {
        } finally {
            outSteam.close();
            inStream.close();
        }
        return outSteam.toByteArray();
    }

}
