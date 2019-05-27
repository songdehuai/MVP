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

    private Project project;
    private VirtualFile selectGroup;
    private String activityFileName = "Activity.java";
    private String fragmentFileName = "Fragment.java";
    private String presenterFilename = "Presenter.java";
    private String contractFileName = "Contract.java";
    private boolean isKotlin = true;

    private InputValidator inputValidator = new InputValidator() {
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
        Pair<String, Boolean> pair = Messages.showInputDialogWithCheckBox("请输入功能名", "MVPCreater", "Kotlin", true, true, Messages.getInformationIcon(), "", inputValidator);
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
        project.getBaseDir().refresh(false, true);
    }

    /**
     * 创建MVP的Base文件夹
     */
    private void createMvpBase() {
        String path = selectGroup.getPath() + "/mvp";
        String packageName = path.substring(path.indexOf("java") + 5, path.length()).replace("/", ".");

        String presenter = FileUtil.readFile(getClass(), isKotlin, "BasePresenter.txt").replace("&package&", packageName);
        String presenterImpl = FileUtil.readFile(getClass(), isKotlin, "BasePresenterImpl.txt").replace("&package&", packageName);
        String view = FileUtil.readFile(getClass(), isKotlin, "BaseView.txt").replace("&package&", packageName);
        String activity = FileUtil.readFile(getClass(), isKotlin, "MVPBaseActivity.txt").replace("&package&", packageName);
        String fragment = FileUtil.readFile(getClass(), isKotlin, "MVPBaseFragment.txt").replace("&package&", packageName);

        FileUtil.writetoFile(presenter, path, "BasePresenter.java");
        FileUtil.writetoFile(presenterImpl, path, "BasePresenterImpl.java");
        FileUtil.writetoFile(view, path, "BaseView.java");
        FileUtil.writetoFile(activity, path, "MVPBaseActivity.java");
        FileUtil.writetoFile(fragment, path, "MVPBaseFragment.java");

    }

    /**
     * 创建MVP架构
     */
    private void createClassMvp(String className) {
        boolean isFragment = className.endsWith("Fragment") || className.endsWith("fragment");
        if (className.endsWith("Fragment") || className.endsWith("fragment") || className.endsWith("Activity") || className.endsWith("activity")) {
            className = className.substring(0, className.length() - 8);
        }
        //project目录
        String path = selectGroup.getPath() + "/" + className.toLowerCase();
        String packageName = path.substring(path.indexOf("java") + 5, path.length()).replace("/", ".");

        String mvpPath = FileUtil.traverseFolder(path.substring(0, path.indexOf("java")));
        mvpPath = mvpPath.substring(mvpPath.indexOf("java") + 5, mvpPath.length()).replace("/", ".").replace("\\", ".");

        className = className.substring(0, 1).toUpperCase() + className.substring(1);

        System.out.print(mvpPath + "---" + className + "----" + packageName);

        //创建Contract
        String contract = FileUtil.readFile(getClass(), isKotlin, "Contract.txt").replace("&package&", packageName).replace("&mvp&", mvpPath).replace("&Contract&", className + "Contract");
        //创建Presenter
        String presenter = FileUtil.readFile(getClass(), isKotlin, "Presenter.txt").replace("&package&", packageName).replace("&mvp&", mvpPath).replace("&Contract&", className + "Contract").replace("&Presenter&", className + "Presenter");
        if (isFragment) {
            //创建Fragment
            String fragment = FileUtil.readFile(getClass(), isKotlin, "Fragment.txt").replace("&package&", packageName).replace("&mvp&", mvpPath).replace("&Fragment&", className + "Fragment").replace("&Contract&", className + "Contract").replace("&Presenter&", className + "Presenter");
            FileUtil.writetoFile(fragment, FileUtil.checkFilePath(selectGroup, path, "fragment"), className + fragmentFileName);
        } else {
            //创建Activity
            String activity = FileUtil.readFile(getClass(), isKotlin, "Activity.txt").replace("&package&", packageName).replace("&mvp&", mvpPath).replace("&Activity&", className + "Activity").replace("&Contract&", className + "Contract").replace("&Presenter&", className + "Presenter");
            FileUtil.writetoFile(activity, FileUtil.checkFilePath(selectGroup, path, "activity"), className + activityFileName);
        }
        FileUtil.writetoFile(contract, FileUtil.checkFilePath(selectGroup, path, "contract"), className + contractFileName);
        FileUtil.writetoFile(presenter, FileUtil.checkFilePath(selectGroup, path, "presenter"), className + presenterFilename);
    }

}
