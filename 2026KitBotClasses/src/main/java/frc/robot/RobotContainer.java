// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import static frc.robot.Constants.OperatorConstants.*;

// import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Constants.OIConstants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.commands.Eject;
import frc.robot.commands.Intake;
import frc.robot.commands.LaunchSequence;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.CANFuelSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems
  private final CANDriveSubsystem m_robotDrive = new CANDriveSubsystem();
  private final CANFuelSubsystem fuelSubsystem = new CANFuelSubsystem();

  // The driver's controller
  private final CommandXboxController m_driverController = new CommandXboxController(
      DRIVER_CONTROLLER_PORT);

  // The operator's controller
  private final CommandXboxController operatorController = new CommandXboxController(
      OPERATOR_CONTROLLER_PORT);

  // The autonomous chooser
  private final SendableChooser<Command> autoChooser;
  // private Command m_autonPath = new PathPlannerAuto("New Auto"); // name what auto is named

  // i can yeet this bit above out if im doing just autochooser automatically

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    configureBindings();
    configureNamedCommand();
    m_robotDrive.setDefaultCommand(
        // The left stick controls translation of the robot.
        // Turning is controlled by the X axis of the right stick.
        new RunCommand(
            () -> m_robotDrive.drive(
                -MathUtil.applyDeadband(m_driverController.getLeftY(), OIConstants.kDriveDeadband),
                -MathUtil.applyDeadband(m_driverController.getLeftX(), OIConstants.kDriveDeadband),
                -MathUtil.applyDeadband(m_driverController.getRightX(), OIConstants.kDriveDeadband),
                false),
            m_robotDrive));

    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Autonomous Selection",autoChooser);
  }
    // Set the options to show up in the Dashboard for selecting auto modes. If you
    // add additional auto modes you can add additional lines here with
    // autoChooser.addOption


    //autoChooser.setDefaultOption("Autonomous", new ExampleAuto(driveSubsystem, fuelSubsystem));

  private void configureBindings() {

    // While the left bumper on operator controller is held, intake Fuel
    operatorController.leftBumper().whileTrue(new Intake(fuelSubsystem));
    // While the right bumper on the operator controller is held, spin up for 1
    // second, then launch fuel. When the button is released, stop.
    operatorController.rightBumper().whileTrue(new LaunchSequence(fuelSubsystem));
    // While the A button is held on the operator controller, eject fuel back out
    // the intake
    operatorController.a().whileTrue(new Eject(fuelSubsystem));

    // Set the default command for the drive subsystem to the command provided by
    // factory with the values provided by the joystick axes on the driver
    // controller. The Y axis of the controller is inverted so that pushing the
    // stick away from you (a negative value) drives the robot forwards (a positive
    // value)

    fuelSubsystem.setDefaultCommand(fuelSubsystem.run(() -> fuelSubsystem.stop()));
  }
  private void configureNamedCommand() {
    NamedCommands.registerCommand("eject", new Eject(fuelSubsystem));
    NamedCommands.registerCommand("intake", new Intake(fuelSubsystem));
    NamedCommands.registerCommand("launch", new LaunchSequence(fuelSubsystem));
    // NamedCommands.registerCommand("stop", m_robotDrive.getDefaultCommand());
    // then i need an explicit "stop moving" thing. lets see if i haveo ne
  } 
  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // the following code is entirely kidnapped from neutrino
    //Command auto;

    //try {

      //auto = m_autonPath;

    //} catch (Exception e) {

      // DO NOT CHANGE THE CODE IN THIS CATCH BLOCK

      //System.err.println("Caught exception when loading auto");

      //auto = new PathPlannerAuto("Nothing");

    //}

   // return auto;
    return autoChooser.getSelected();

  }

  public Command getZeroGyroCommand() {
    return m_robotDrive.zeroHeadingCommand();
  }
}
