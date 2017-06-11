package Logic;

/**
 * Created by Le Pham Minh Duc on 6/10/2017.
 */

public class SetupLogic {

    private String instructionText_reading_instruction =
            "Welcome to the stroke arm test application.\n" +
            "Please read these instructions carefully.\n" +
            "You will be asked to perform some actions to calibrate your data.\n" +
            "Move your phone to the requested position and keep the phone still for 5 seconds\n" +
            "Press START to start the setup, press CANCEL to go back to the main screen.";
    private String instructionText_right_hand_down =
            "Hold the phone in your RIGHT hand and move it \n" +
            "to the lowest possible position of your body";
    private String instructionText_right_hand_front =
            "Hold the phone in your RIGHT hand and move it \n" +
            "to the front of your body";
    private String instructionText_right_hand_up =
            "Hold the phone in your RIGHT hand and move it \n" +
            "to the highest possible position of your body above your head";
    private String instructionText_left_hand_down =
            "Hold the phone in your LEFT hand and move it \n" +
            "to the lowest possible position of your body";
    private String instructionText_left_hand_front =
            "Hold the phone in your LEFT hand and move it \n" +
            "to the front of your body";
    private String instructionText_left_hand_up =
            "Hold the phone in your LEFT hand and move it \n" +
            "to the highest possible position of your body above your head";
    private String instructionText_finish =
            "Set up completed, all your data have been saved\n" +
            "Thanks for your time. :)";

    //-1 = not set up, 1 = complete ? maybe, just left a string so we have more option.
    public String setup_status;

    //data will be saved in a json format, and then stored as string
    public String right_hand_down_position = "RIGHT_HAND_DOWN";
    public String right_hand_front_position = "RIGHT_HAND_FRONT";
    public String right_hand_up_position = "RIGHT_HAND_UP";

    public String left_hand_down_position = "LEFT_HAND_DOWN";
    public String left_hand_front_position = "LEFT_HAND_FRONT";
    public String left_hand_up_position = "LEFT_HAND_UP";

    //singleton
    private static SetupLogic instance;
    public static SetupLogic getInstance() {
        if (instance == null) {
            instance = new SetupLogic();
        }
        return instance;
    }

    private SetupStep currentSetupStep = SetupStep.Not_Initialized;

    private SetupLogic() {
        currentSetupStep = SetupStep.Reading_Instruction;
    }

    public void prepareLogic() {
        currentSetupStep = SetupStep.Reading_Instruction;
    }

    public SetupStep getCurrentSetupStep () {
        return currentSetupStep;
    }

    public String getInstructionText() {
        switch (currentSetupStep) {
            case Not_Initialized:
                return "The setup is not initialized, something must be wrong. Please contact developer!!!";
            case Reading_Instruction:
                return instructionText_reading_instruction;
            case Right_Hand_Down:
                return instructionText_right_hand_down;
            case Right_Hand_Front:
                return instructionText_right_hand_front;
            case Right_Hand_Up:
                return instructionText_right_hand_up;
            case Left_Hand_Down:
                return instructionText_left_hand_down;
            case Left_Hand_Front:
                return instructionText_left_hand_front;
            case Left_Hand_Up:
                return instructionText_left_hand_up;
            case Finish:
                return instructionText_finish;
            default:
                return "Set up step not defined !!!!, please contact developer !!!";
        }
    }

    public void toNextStep() {
        currentSetupStep = getNextStep(currentSetupStep);
    }

    public boolean isSetupFinish() {
        return currentSetupStep == SetupStep.Finish;
    }

    private SetupStep getNextStep(SetupStep step) {
        switch (step) {
            case Not_Initialized:
                return SetupStep.Finish;

            case Reading_Instruction:
                return SetupStep.Right_Hand_Down;
            case Right_Hand_Down:
                return SetupStep.Right_Hand_Front;
            case Right_Hand_Front:
                return SetupStep.Right_Hand_Up;
            case Right_Hand_Up:
                return SetupStep.Left_Hand_Down;
            case Left_Hand_Down:
                return SetupStep.Left_Hand_Front;
            case Left_Hand_Front:
                return SetupStep.Left_Hand_Up;
            case Left_Hand_Up:
                return SetupStep.Finish;

            default:
                return SetupStep.Finish;
        }
    }

    public String getCurrentSetupKey() {
        return getSetupKey(currentSetupStep);
    }

    public String getSetupKey(SetupStep step) {
        switch (step) {
            case Not_Initialized:
            case Reading_Instruction:
            case Finish:
            default:
                return "Invalid";

            case Right_Hand_Down:
                return right_hand_down_position;
            case Right_Hand_Front:
                return right_hand_front_position;
            case Right_Hand_Up:
                return right_hand_up_position;
            case Left_Hand_Down:
                return left_hand_down_position;
            case Left_Hand_Front:
                return left_hand_front_position;
            case Left_Hand_Up:
                return left_hand_up_position;
        }
    }
}
