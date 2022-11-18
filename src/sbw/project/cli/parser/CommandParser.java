package sbw.project.cli.parser;

import sbw.architecture.datatype.*;
import sbw.project.cli.action.ActionSet;
import sbw.project.cli.action.command.misc.*;
import sbw.project.cli.action.ActionCreational.*;

import java.util.ArrayList;

public class CommandParser {
    ActionSet actionSet;
    String command;

    public CommandParser(ActionSet actionSet, String command) {
        this.actionSet = actionSet;
        this.command = command;
    }

    public void parse() {
        String[] commands = this.command.split(";");

        for(String command: commands) {
            String[] commandArr = command.split(" ");

            assert commandArr.length > 0;

            // Check first word to find type of command
            if(getCommandType(commandArr[0]).equalsIgnoreCase("MISC")) {
                miscHandler(commandArr);
            }
            else if(getCommandType(commandArr[0]).equalsIgnoreCase("CREATIONAL")) {
                creationalHandler(commandArr);
            }
            else if(getCommandType(commandArr[0]).equalsIgnoreCase("STRUCTURAL")) {
                structuralHandler(commandArr);
            }
        }
    }

    // Given the first word of the command : Returns the command type
    private String getCommandType(String cmd) {
        return switch (cmd) {
            case "@EXIT", "@CLOCK" -> "MISC";
            case "CREATE" -> "CREATIONAL";
            case "DECLARE", "COMMIT" -> "STRUCTURAL";
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
//        else if(part.equalsIgnoreCase("ELEVATOR")) {
//            this.actionSet.getActionCreational().doCreateElevator(id, upLimit, speed, acc);
//        }
//        else if(part.equalsIgnoreCase("AILERON")) {
//            this.actionSet.getActionCreational().doCreateAileron(id, upLimit, downLimit, speed, acc);
//        }
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

    public void structuralHandler(String[] commandArr) {
        if(commandArr[0].equalsIgnoreCase("COMMIT")) {
            this.actionSet.getActionStructural().doCommit();
        }
        else {
            String part = commandArr[1];
            Identifier id1 = null;
            ArrayList<Identifier> idn = new ArrayList<>();
            if(part.equalsIgnoreCase("BUS")) {
                id1 = new Identifier(commandArr[2]);
            }
            else {
                id1 = new Identifier(commandArr[3]);
            }
            if(part.equalsIgnoreCase("GEAR")) {
                idn.add(new Identifier(commandArr[7]));
                idn.add(new Identifier(commandArr[9]));
                idn.add(new Identifier(commandArr[10]));
            }
            else {
                for(int i = 5; i < commandArr.length; i++) {
                    idn.add(new Identifier(commandArr[i]));
                }
            }
            callStructuralCommand(part, id1, idn);
        }
    }

    public void callStructuralCommand(String part, Identifier id1, ArrayList<Identifier> idn) {
        if(part.equalsIgnoreCase("RUDDER")) {
            this.actionSet.getActionStructural().doDeclareRudderController(id1, idn.get(0));
        }
        else if(part.equalsIgnoreCase("FLAP")) {
            this.actionSet.getActionStructural().doDeclareFlapController(id1, idn);
        }
        else if(part.equalsIgnoreCase("ENGINE")) {
            this.actionSet.getActionStructural().doDeclareEngineController(id1, idn);
        }
        else if(part.equalsIgnoreCase("GEAR")) {
            this.actionSet.getActionStructural().doDeclareGearController(id1, idn.get(0), idn.get(1), idn.get(2));
        }
        else if(part.equalsIgnoreCase("BUS")) {
            this.actionSet.getActionStructural().doDeclareBus(id1, idn);
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
