/* 
 * OdometryCorrection.java
 */
package ev3Odometer;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.Sound;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer 		odometer;
	private int 			currentRedValue;
	private final int 		lineDetectionValue = 40;
	private final double 	colorSensorDisplacement = 3.7;
	private final int 		distanceError =6;
	private final int 		angleError = 20;
	private final double 	squareSize = 30.48;
	public static int		xCorrectionCount =0;
	public static int		yCorrectionCount =0;
	
	private EV3ColorSensor 	colorSensor = new EV3ColorSensor(SensorPort.S4);
	private float[] 		sampleRed = {0};
	
	// constructor
	public OdometryCorrection(Odometer pOdometer) {
		odometer = pOdometer;
		colorSensor.setFloodlight(true);
		
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		
		while (true) {
			correctionStart = System.currentTimeMillis();
			
			//fetch a sample from the sensor
			colorSensor.getRedMode().fetchSample(sampleRed, 0);
			currentRedValue = (int)(sampleRed[0]*100);

			//if we run over a red line, calculate and update odometer values
			if(currentRedValue < lineDetectionValue)
				updateOdometerForClockWiseSquare(squareSize);
			
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
	
	public int getCurrentRedValue() {
		return currentRedValue;
	}

	/*
	 *This method determines the robots location on the square by using the theta and x,y values. Once it has 
	 *determined it's location and the current line that it is crossing, it updates that variable accordingly 
	 */
	private void updateOdometerForClockWiseSquare(double pSquareSize)
	{
		double squareSize = pSquareSize;
		double tempX = odometer.getX();
		double tempY = odometer.getY();
		double tempTheta = odometer.getTheta()*180/Math.PI;

		//The robot is on the first side of the square
		if(Math.abs(tempTheta) < angleError )
		{
			if( tempY < squareSize/2 - colorSensorDisplacement + distanceError && tempY > squareSize/2 - colorSensorDisplacement - distanceError )
			{
				Sound.beep();
				odometer.setY(squareSize/2 - colorSensorDisplacement);
				yCorrectionCount ++;
			}
			
			if( tempY < squareSize + squareSize/2 + colorSensorDisplacement + distanceError && tempY > squareSize + squareSize/2 + colorSensorDisplacement - distanceError )
			{
				Sound.beep();
				odometer.setY(squareSize + squareSize/2 - colorSensorDisplacement);
				yCorrectionCount ++;
			}
		}
		
		//The robot is on the second side of the square
		if(tempTheta < 90 + angleError && tempTheta > 90 - angleError )
		{
			if( tempX < squareSize/2 - colorSensorDisplacement + distanceError && tempX > squareSize/2 - colorSensorDisplacement - distanceError )
			{
				Sound.beep();				
				odometer.setX(squareSize/2 - colorSensorDisplacement);
				xCorrectionCount ++;
			}
			if( tempX < squareSize + squareSize/2 - colorSensorDisplacement + distanceError && tempX > squareSize + squareSize/2 - colorSensorDisplacement - distanceError )
			{
				Sound.beep();
				odometer.setX(squareSize + squareSize/2 - colorSensorDisplacement);
				xCorrectionCount ++;
			}
			
		}
		
		//The robot is on the third side of the square
		if(tempTheta < 180 + angleError && tempTheta > 180 - angleError )
		{
			if( tempY < squareSize/2 + colorSensorDisplacement + distanceError && tempY > squareSize/2 + colorSensorDisplacement - distanceError )
			{
				Sound.beep();
				odometer.setY(squareSize/2 + colorSensorDisplacement);
				yCorrectionCount ++;
			}
			
			if( tempY < squareSize + squareSize/2 + colorSensorDisplacement + distanceError && tempY > squareSize + squareSize/2 + colorSensorDisplacement - distanceError )
			{
				Sound.beep();
				odometer.setY(squareSize + squareSize/2 + colorSensorDisplacement);
				yCorrectionCount ++;
			}
			
		}

		//The robot is on the fourth side of the square
		if(tempTheta < 270 + angleError && tempTheta > 270 - angleError )
		{
			if( tempX < squareSize/2 + colorSensorDisplacement + distanceError && tempX > squareSize/2 + colorSensorDisplacement - distanceError )
			{
				Sound.beep();
				odometer.setX(squareSize/2 + colorSensorDisplacement);
				xCorrectionCount ++;
			}

			if( tempX < squareSize + squareSize/2 + colorSensorDisplacement + distanceError && tempX > squareSize + squareSize/2 + colorSensorDisplacement - distanceError )
			{
				Sound.beep();
				odometer.setX(squareSize + squareSize/2 + colorSensorDisplacement);
				xCorrectionCount ++;
			}

		}
	}
}