package com.example.demo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class MyNotice extends AnAction {
    private static final String MESSAGE = "Sure to clear hot deploy?";
    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODO: insert action logic here
        Project project = event.getData(PlatformDataKeys.PROJECT);
        int txt = Messages.showOkCancelDialog(MESSAGE, "Clear hotDeploy", Messages.getOkButton(), Messages.getCancelButton(), Messages.getInformationIcon());
        Messages.showMessageDialog(project, String.valueOf(txt), "Result", Messages.getInformationIcon());

    }
}
