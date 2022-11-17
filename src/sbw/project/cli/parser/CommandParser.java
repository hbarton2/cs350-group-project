package sbw.project.cli.parser;

import sbw.architecture.datatype.*;
import sbw.project.cli.action.ActionSet;
import sbw.project.cli.action.command.misc.*;
import sbw.project.cli.action.ActionCreational.*;

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
        else if(getCommandType(commandArr[0]).equalsIgnoreCase("CREATIONAL")) {
            creationalHandler(commandArr);
        }
    }

    // Given the first word of the command : Returns the command type
    private String getCommandType(String cmd) {
        return switch (cmd) {
            case "@EXIT", "@CLOCK" -> "MISC";
            case "CREATE" -> "CREATIONAL";
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

    private void creationalHandler(String[] commandArr) {
        String part = "";
        Identifier id = null;
        Angle upLimit = null;
        Angle downLimit = null;
        Speed speed = null;
        Acceleration acc = null;

        for(int i = 0; i < commandArr.length - 1; i++) {
            if(commandArr[i].equalsIgnoreCase("CREATE")) {
                part = commandArr[i + 1];
                id = new Identifier(commandArr[i + 2]);
            }
            else if(commandArr[i].equalsIgnoreCase("LIMIT")) {
                Double par = tryParsing(commandArr[i + 1]);

                if(par != null) {
                    upLimit = new Angle(par);
                }
                else {
                    par = tryParsing(commandArr[i + 2]);
                    if(upLimit == null) {
                        upLimit = new Angle(par);
                    }
                    else {
                        downLimit = new Angle(par);
                    }
                }
            }
            else if(commandArr[i].equalsIgnoreCase("SPEED")) {
                speed = new Speed(Double.parseDouble(commandArr[i + 1]));
            }
            else if(commandArr[i].equalsIgnoreCase("ACCELERATION")) {
                acc = new Acceleration(Double.parseDouble(commandArr[i + 1]));
            }
        }
        callCreationalCommand(part, id, upLimit, downLimit, speed, acc);
    }

    private void callCreationalCommand(String part, Identifier id, Angle upLimit, Angle downLimit, Speed speed, Acceleration acc) {
        if(part.equalsIgnoreCase("RUDDER")) {
            this.actionSet.getActionCreational().doCreateRudder(id, upLimit, speed, acc);
        }
        else if(part.equalsIgnoreCase("ELEVATOR")) {
            this.actionSet.getActionCreational().doCreateElevator(id, upLimit, speed, acc);
        }
        else if(part.equalsIgnoreCase("AILERON")) {
            this.actionSet.getActionCreational().doCreateAileron(id, upLimit, downLimit, speed, acc);
        }
        else if(part.equalsIgnoreCase("FOWLER")) {
            this.actionSet.getActionCreational().doCreateFlap(id, true, upLimit, speed, acc);
        }
        else if(part.equalsIgnoreCase("SPLIT")) {
            this.actionSet.getActionCreational().doCreateFlap(id, false, upLimit, speed, acc);
        }
        else if(part.equalsIgnoreCase("ENGINE")) {
            this.actionSet.getActionCreational().doCreateEngine(id, speed, acc);
        }
        else if(part.equalsIgnoreCase("NOSE")) {
            this.actionSet.getActionCreational().doCreateGearNose(id, speed, acc);
        }
        else if(part.equalsIgnoreCase("MAIN")) {
            this.actionSet.getActionCreational().doCreateGearMain(id, speed, acc);
        }
    }

    private Double tryParsing(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
