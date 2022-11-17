package sbw.project.cli.parser;

import sbw.architecture.datatype.*;
import sbw.project.cli.action.ActionSet;
import sbw.project.cli.action.command.misc.*;

public class CommandParser {
    ActionSet actionSet;
    String command;

    public CommandParser(ActionSet actionSet, String command) {
        this.actionSet = actionSet;
        this.command = command;
    }

    public void parse() {
        String[] commandArr = this.command.split(" ");

        assert commandArr.length > 0;

        // Check first word to find type of command
        if(getCommandType(commandArr[0]).equalsIgnoreCase("MISC")) {
            miscHandler(commandArr);
        }
    }

    // Given the first word of the command : Returns the command type
    private String getCommandType(String cmd) {
        return switch (cmd) {
            case "@EXIT", "@CLOCK" -> "MISC";
            default -> "";
        };
    }

    // Executes a miscellaneous command
    private void miscHandler(String[] commandArr) {
        if(commandArr[0].equalsIgnoreCase("@EXIT")) {
            this.actionSet.getActionMiscellaneous().submitCommand(new CommandDoExit());
        }
        else if (commandArr[0].equalsIgnoreCase("@CLOCK")
                && commandArr.length == 1) {
            this.actionSet.getActionMiscellaneous().submitCommand(new CommandDoShowClock());
        }
        else if (commandArr[0].equalsIgnoreCase("@CLOCK")) {
            Rate rate = new Rate(Integer.parseInt(commandArr[1]));
            this.actionSet.getActionMiscellaneous().submitCommand(new CommandDoSetClockRate(rate));
        }
    }
}
