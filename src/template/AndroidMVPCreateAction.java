package template;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;


public class AndroidMVPCreateAction extends AnAction {

    private Project project;
    private VirtualFile selectGroup;

    private enum FileType {
        activity,
        fragment,
        presenter,
        contract
    }

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
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        project = anActionEvent.getProject();
        String className = "";
        Pair<String, Boolean> pair = Messages.showInputDialogWithCheckBox("请输入功能名", "MVPCreater", "Kotlin", true, true, Messages.getInformationIcon(), "", inputValidator);
        className = pair.first;
        isKotlin = pair.second;
        if (isKotlin) {

        } else {

        }
        selectGroup = DataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
        if (className == null || className.equals("")) {
            System.out.print("没有输入类名");
            return;
        }
        if (className.equalsIgnoreCase("mvp")) {
            //创建BaseMVP
            createBase();
        } else {
            //创建架构代码
            createMVP(className);
        }
        //刷新目录结构
        project.getBaseDir().refresh(false, true);
    }


    private String activityPath = "";
    private String fragmentPath = "";
    private String presenterPath = "";
    private String contractPath = "";

    public void createMVP(String className) {
        String oldCalssName = className;
        //选择的包目录
        String path = selectGroup.getPath() + "/" + className.toLowerCase();
        String selectPath = selectGroup.getPath() + "/";
        String packageName = path.substring(path.indexOf("java") + 5, path.length()).replace("/", ".");
        boolean isFragment = className.endsWith("Fragment") || className.endsWith("fragment");
        if (className.endsWith("Fragment") || className.endsWith("fragment") || className.endsWith("Activity") || className.endsWith("activity")) {
            className = className.substring(0, className.length() - 8);
        }
        className = className.substring(0, 1).toUpperCase() + className.substring(1);
        //MVP架构目录
        String mvpPath = FileUtil.traverseFolder(path.substring(0, path.indexOf("java")));
        mvpPath = mvpPath.substring(mvpPath.indexOf("java") + 5, mvpPath.length()).replace("/", ".").replace("\\", ".");

        //创建Activity/Fragment
        if (isFragment) {
            String tempClassName = className;
            //创建Fragment
            if (FileUtil.pathDirExists(selectPath + "/fragment")) {
                //存在
                fragmentPath = selectPath + "/fragment";
                tempClassName = className.replace(oldCalssName.toLowerCase() + ".", "");
            } else {
                fragmentPath = path + "/fragment";
            }
            String fragment = FileUtil.readFile(getClass(), isKotlin, "Fragment.txt")
                    .replace("&package&", packageName)
                    .replace("&mvp&", mvpPath)
                    .replace("&Fragment&", tempClassName + "Fragment")
                    .replace("&Contract&", tempClassName + "Contract")
                    .replace("&Presenter&", tempClassName + "Presenter");
            FileUtil.writetoFile(fragment, fragmentPath, tempClassName + "Fragment.kt");
        } else {
            activityPath = selectPath;
            String tempClassName = className.replace(oldCalssName.toLowerCase() + ".", "");
            String activity = FileUtil.readFile(getClass(), isKotlin, "Activity.txt")
                    .replace("&package&", packageName)
                    .replace("&mvp&", mvpPath)
                    .replace("&Activity&", tempClassName + "Activity")
                    .replace("&Contract&", tempClassName + "Contract")
                    .replace("&Presenter&", tempClassName + "Presenter");
            FileUtil.writetoFile(activity, activityPath, tempClassName + "Activity.kt");
        }
        //创建Presenter
        String tempClassName = className;
        if (FileUtil.pathDirExists(selectPath + "/presenter")) {
            presenterPath = selectPath + "/presenter";
            tempClassName = className.replace(oldCalssName.toLowerCase() + ".", "");
        } else {
            presenterPath = path + "/presenter";
        }
        String presenter = FileUtil.readFile(getClass(), isKotlin, "Presenter.txt")
                .replace("&package&", packageName)
                .replace("&mvp&", mvpPath)
                .replace("&Contract&", tempClassName + "Contract")
                .replace("&Presenter&", tempClassName + "Presenter");
        FileUtil.writetoFile(presenter, presenterPath, tempClassName + "Presenter.kt");

        String tempContractClassName = className;
        //创建Contract
        if (FileUtil.pathDirExists(selectPath + "/contract")) {
            contractPath = selectPath + "/contract";
            tempContractClassName = className.replace(oldCalssName.toLowerCase() + ".", "");
        } else {
            contractPath = path + "/contract";
        }
        String contract =
                FileUtil.readFile(getClass(), isKotlin, "Contract.txt")
                        .replace("&package&", packageName)
                        .replace("&mvp&", mvpPath)
                        .replace("&Contract&", tempContractClassName + "Contract");
        FileUtil.writetoFile(contract, contractPath, tempContractClassName + "Contract.kt");
    }

    /**
     * 创建MVPBase架构
     */
    public void createBase() {
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

    public void getFileName() {

    }

}
