package frc.robot;


import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.spark.config.AbsoluteEncoderConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import frc.robot.Constants.ModuleConstants;


public final class Configs {
  public static final class MAXSwerveModule {
    public static final SparkMaxConfig drivingConfig = new SparkMaxConfig();
    public static final SparkMaxConfig turningConfig = new SparkMaxConfig();
    

    static {
      // Use module constants to calculate conversion factors and feed forward gain.
      double drivingFactor =
          ModuleConstants.kWheelDiameterMeters * Math.PI / ModuleConstants.kDrivingMotorReduction;
      double turningFactor = 2 * Math.PI;
      double nominalVoltage = 12.0; // xxx is that right
      double drivingVelocityFeedForward = nominalVoltage / ModuleConstants.kDriveWheelFreeSpeedRps;
// in the line above, nominalVoltage takes the place of 1 xxx

      drivingConfig.idleMode(IdleMode.kBrake).smartCurrentLimit(50);
      // xxx previously had stallLimit:30 instead of 50, what is the relevance of this
      drivingConfig
          .encoder
          .positionConversionFactor(drivingFactor) // meters
          .velocityConversionFactor(drivingFactor / 60.0); // meters per second
      drivingConfig.closedLoop
          .feedbackSensor(FeedbackSensor.kPrimaryEncoder) //xxx need to make encoder object?
          // These are example gains you may need to them for your own robot!
          // xxx should probably test/change these?
          .pid(0.04, 0, 0)
          .outputRange(-1, 1)
          .feedForward.kV(drivingVelocityFeedForward);
// xxx note: gyro is physically located on roborio

      turningConfig.idleMode(IdleMode.kBrake).smartCurrentLimit(20);
      // was previously 10 xxx
      turningConfig//xxx tells motor what things are ini t. this wholefile from revrobotics github
          .absoluteEncoder // xxx neutrino wonderful person said we might not have one
          // but the revrobotics github says its fine this way so im gonna leave it

          // Invert the turning encoder, since the output shaft rotates in the opposite
          // direction of the steering motor in the MAXSwerve Module.
          .inverted(true)
          .positionConversionFactor(turningFactor) // radians
          .velocityConversionFactor(turningFactor / 60.0) // radians per second
          .apply(AbsoluteEncoderConfig.Presets.REV_ThroughBoreEncoderV2);
      turningConfig.closedLoop
          .feedbackSensor(FeedbackSensor.kAbsoluteEncoder)
          // These are example gains you may need to them for your own robot! 
          .pid(1, 0, 0)
          .outputRange(-1, 1)
          // Enable PID wrap around for the turning motor. This will allow the PID
          // controller to go through 0 to get to the setpoint i.e. going from 350 degrees
          // to 10 degrees will go through 0 rather than the other direction which is a
          // longer route.
          .positionWrappingEnabled(true)
          .positionWrappingInputRange(0, turningFactor);
    }
  }


}
