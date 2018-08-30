package cs;
import robocode.*;
//import java.awt.Color;

/**
 * MmBot - a robot by Mathias D. Andersen &
 */

public class SolliPizzaGrill extends AdvancedRobot {
	
public String Target;
	public byte moveDir = 1;
	public byte scanDir = 1;
	public int searchTime = 0;
	
	public void run() {
		
		setAdjustRadarForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		//setColors(Color.DARK_GRAY, Color.GRAY, Color.BLUE, Color.ORANGE, Color.LIGHT_GRAY);
		
		while(true) {
			scan();
			searchTime++;
			turnRadarRight(360/4);
			
			if(Target != null || searchTime > 5){
				Target = null;
				searchTime = 0;
			}
			
			if(getVelocity() > 0){
				moveDir *= -1;
			}
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
	
		if (Target != null && !e.getName().equals(Target)) {
			return;
		}
		
		if(e.getName() == Target){
			setTurnRadarRight(getHeading() - getRadarHeading() + e.getBearing()); // Holde radaren på target.
			scanDir *= -1; // -- == --
			setTurnRadarRight(360 * scanDir); // Prøve å holde radaren i ro på target.
			setTurnGunRight(getHeading() - getGunHeading() + e.getBearing()); // Holde skyteren mot target.
			
			// Velger om roboten skal sirklere innover mot vår Target eller bare sirklere.
			if(e.getDistance() > 100){
				// Sirklere innover mot target
				setTurnRight(e.getBearing() + 90 - (1 * moveDir));
			}
			else{
				// Gå rundt vår target
				setTurnRight(e.getBearing() - 90); // Sette kroppen sideveis.
			}
			
			// Gå fremover.
			setAhead((e.getDistance() * moveDir));
			
			// Hvis ikke skyteren ikke er varm og vi sikter på target og distanc er mindre enn 200, skyt!.
			if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10 && e.getDistance() < 200){
				// Kalkulering av bullet velocity og firepower.
				double firePower = Math.min(500 / e.getDistance(), 3);
				double bulletSpeed = 20 - firePower * 3;
				setFire(e.getDistance() / bulletSpeed);
			}
		}else if(Target == null){
			Target = e.getName();
		}
	}
	
	@Override
	public void onBulletHit(BulletHitEvent e) {
		if(Target == e.getName()){
			searchTime = 0;
		}
	}

	public void onHitByBullet(HitByBulletEvent e) {
		// Setter den som skyter oss som target.
		Target = e.getName();
	}
	
	public void onHitWall(HitWallEvent e) {
		// Skifter retning hvis vi treffer en vekk.
		moveDir *= -1;
	}
	
	public void onHitRobot(HitRobotEvent event){
		
		if (event.isMyFault()) {
			moveDir *= -1;
		}
		
		if(searchTime > 5){
			Target = event.getName();	
			searchTime = 0;
		}
	}
	
	public void onWin(WinEvent event){
		for(int yolo = 0; yolo < 100; yolo = yolo + 4){
			turnLeft(yolo * 2);
		}
	}
}
