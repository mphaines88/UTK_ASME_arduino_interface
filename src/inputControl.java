/**
 * Created by USER on 7/12/2015.
 */

import messaging.KillMessage;
import net.java.games.input.*;
import messaging.IMessage;
import messaging.MotorMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class inputControl {

    private boolean isRunning = true;
    private List<String> availableControllers = new ArrayList<String>();
    Controller[] ca;
    Controller xboxController;

    public inputControl(){
        ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
        xboxController = null;
        for (Controller aCa : ca) {
            //System.out.println(ca[i].getName());
            if (aCa.getName().equals("XBOX 360 For Windows (Controller)") || aCa.getName().equals("Controller (XBOX 360 For Windows)")) {
                xboxController = aCa;
                //System.out.println(xboxController.getName());
                break;
            }
        }

        if (xboxController == null){
            System.out.println("no xbox controller found");
        }
    }

    public Controller[] getName(){
        return ca;
    }

    public void controllerControl(Controller controller) {

        xboxController = controller;
        double xaxis = 0;
        double yaxis = 0;
        double xrotation = 0;
        double yrotation = 0;
        double aux_flipper_val = .6;
        boolean isKilled = false;
        boolean combineIsOn = false;
        double powerfactor = 1;
        boolean guessmode = false;

        //Konami Code Variables
        byte[] Konamicode;

        while (true) {
            while (isRunning) {
                xboxController.poll();
                EventQueue queue = xboxController.getEventQueue();
                Event event = new Event();
                while (queue.getNextEvent(event)) {
                    float value = event.getValue();
                    switch (event.getComponent().toString()) {

                        case "Button 0":
                            if (value == 1.0f) {
                                writeMessage(new MotorMessage(MotorMessage.Motor.COMBINE_MOTOR, 1));
                                //System.err.println("A Button On");
                            } else {
                                //System.out.println("A Button Off");
                                continue;
                            }
                            break;


                        case "Button 1":
                            if (value == 1.0f) {
                                writeMessage(new MotorMessage(MotorMessage.Motor.COMBINE_MOTOR, -1));
                                //System.err.println("B Button On");
                            } else {
                                //System.out.println("B Button Off");
                                continue;
                            }
                            break;

                        case "Button 2":
                            if (value == 1.0f) {
                                if (aux_flipper_val > 0) {
                                    aux_flipper_val -= .2;
                                }
                                //System.out.println("X Button On");
                            } else {
                                continue;
                                //System.out.println("X Button Off");
                            }
                            break;

                        case "Button 3":
                            if (value == 1.0f) {
                                if (aux_flipper_val < 1) {
                                    aux_flipper_val += .2;
                                }
                                //System.out.println("Y Button On");
                            } else {
                                continue;
                                //System.out.println("Y Button Off");
                            }
                            break;

                        case "Button 4":
                            if (value == 1.0f) {
                                writeMessage(new MotorMessage(MotorMessage.Motor.HOPPER_MOTOR, -1 * aux_flipper_val));
                                //System.out.println("Left Bumper On");
                            } else {
                                writeMessage(new MotorMessage(MotorMessage.Motor.HOPPER_MOTOR, 0));
                                //System.out.println("Left Bumper Off");
                            }
                            break;

                        case "Button 5":
                            if (value == 1.0f) {
                                writeMessage(new MotorMessage(MotorMessage.Motor.HOPPER_MOTOR, aux_flipper_val));
                                //System.out.println("Right Bumper On");
                            } else {
                                writeMessage(new MotorMessage(MotorMessage.Motor.HOPPER_MOTOR, 0));
                                //System.out.println("Right Bumper Off");
                            }
                            break;

                        case "Button 6":
                            if (value == 1.0f) {
                                if (!guessmode) {
                                    powerfactor = .7;
                                    guessmode = true;
                                    System.out.println("Guest Mode Enabled!");
                                } else {
                                    powerfactor = 1;
                                    guessmode = false;
                                    System.out.println("Guest Mode Disabled!");
                                }
                            } else {
                                continue;
                            }
                            break;

                        case "Button 7":
                            if (value == 1.0f) {

                                if (!isKilled) {
                                    writeMessage(new KillMessage(1));
                                    isKilled = true;
                                    System.out.println("The robot is dead!");
                                } else {
                                    writeMessage(new KillMessage(0));
                                    isKilled = false;
                                }

                            } else {
                                continue;
                            }
                            break;

                        case "Button 9":
                            if (value == 1.0f && !combineIsOn) {
                                writeMessage(new MotorMessage(MotorMessage.Motor.COMBINE_MOTOR, 1));
                                combineIsOn = true;
                            } else if (value == 1.0f && combineIsOn) {
                                writeMessage(new MotorMessage(MotorMessage.Motor.COMBINE_MOTOR, 0));
                                combineIsOn = false;
                            } else {
                                continue;
                            }

                            break;

                        case "Button 10":
                            break;

                        case "X Axis":
                            break;

                        case "Y Axis":
                            yaxis = value;
                            if (yaxis > .15 || yaxis < -.15) {
                                writeMessage(new MotorMessage(MotorMessage.Motor.LEFT_FRONT_DRIVE_MOTOR, -Math.copySign(1, yaxis) * Math.pow(10, Math.abs(yaxis)) * .1 * powerfactor));
                                //System.out.println(String.format("left stick throttleLeft is %1$s", -Math.copySign(1, yaxis) * Math.pow(10,Math.abs(yaxis)) * .1));
                            } else {
                                writeMessage(new MotorMessage(MotorMessage.Motor.LEFT_FRONT_DRIVE_MOTOR, 0));
                            }
                            break;

                        case "X Rotation":
                            break;

                        case "Y Rotation":
                            yrotation = value;
                            if (yrotation > .15 || yrotation < -.15) {
                                writeMessage(new MotorMessage(MotorMessage.Motor.RIGHT_FRONT_DRIVE_MOTOR, -Math.copySign(1, yrotation) * Math.pow(10, Math.abs(yrotation)) * .1 * powerfactor));
                                //System.out.println(String.format("right stick throttleLeft is %1$s and direction is %2$s", throttleRight, rightDirection));

                            } else {
                                writeMessage(new MotorMessage(MotorMessage.Motor.RIGHT_FRONT_DRIVE_MOTOR, 0));
                            }
                            break;

                        case "Z Axis":
                            //System.out.println(String.format("Z Axis Magnitude is %s", value));
                            writeMessage(new MotorMessage(MotorMessage.Motor.LIFTER_MOTOR, -powerfactor * value));
                            break;

                    }
                }
            }
        }
    }

    public void writeMessage(IMessage msg){
        try {
            byte[] test = msg.getBytes();
            for(int i = 0; i < msg.getBytes().length; i++){
                int positive = test[i] & 0xff;
                //System.out.print(positive);
                //System.out.print(' ');
            }
            //System.out.println(' ');
            serialTest.output.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIsRunning(boolean isRunning){
        this.isRunning = isRunning;

    }

    /*public static void main(String[] args){
    }*/
}
