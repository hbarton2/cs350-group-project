package sbw.project.cli.parser;

import sbw.project.cli.action.ActionSet;

public class CommandParser {
    ActionSet actionSet;
    String command;

    public CommandParser(ActionSet actionSet, String command) {
        this.actionSet = actionSet;
        this.command = command;
    }

    public void parse() {
        System.out.println(command);
    }
}
