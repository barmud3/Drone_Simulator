

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.Instant;
import java.awt.Dimension;
import java.awt.Toolkit;



import javax.swing.*;

public class SimulationWindow {

	private JFrame frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimulationWindow window = new SimulationWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SimulationWindow() {
		initialize();
	}

	public static JLabel info_label;
	public static JLabel timeLabel;
	public static boolean return_home = false;

	boolean toogleStop = true;
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Drone Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Get the screen dimensions
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;

		// Set the frame size to match the screen dimensions
		frame.setSize(screenWidth, screenHeight);

		// Center the frame on the screen (optional)
		frame.setLocationRelativeTo(null);

		// Set layout to null for manual positioning of components
		frame.getContentPane().setLayout(null);

		// Create a label to display "Running time:"
		JLabel runningTimeLabel = new JLabel("Running time:");
		runningTimeLabel.setBounds(screenWidth - 100, 80, 100, 30);
		frame.getContentPane().add(runningTimeLabel);

		// Initialize the time label aligned with the right and bottom edges
		timeLabel = new JLabel();
		timeLabel.setBounds(screenWidth - 100, 100, 200, 30);
		frame.getContentPane().add(timeLabel);

		// Initialize other labels aligned with the right and bottom edges
		info_label = new JLabel();
		info_label.setBounds(screenWidth - 200, screenHeight - 200, 300, 200);
		frame.getContentPane().add(info_label);

		info_label2 = new JLabel();
		info_label2.setBounds(screenWidth - 200, screenHeight - 250, 300, 200);
		frame.getContentPane().add(info_label2);

		main();
	}


	public JLabel info_label2;
	public static boolean toogleRealMap = true;
	public static boolean toogleAI = true;
	
	public static AutoAlgo1 algo1;


	public static Instant startTime;
	public void main() {
		int map_num = 4;
		Point[] startPoints = {
				new Point(100,50),
				new Point(50,60),
				new Point(73,68),
				new Point(84,73),
				new Point(92,100)};
		
		Map map = new Map("C:\\Users\\בר\\OneDrive\\שולחן העבודה\\מדמח\\שנה ג\\רובוטים אוטונומים\\Matala1_Simulator\\Maps\\p1" + map_num + ".png",startPoints[map_num-1]);


		algo1 = new AutoAlgo1(map);
		
		Painter painter = new Painter(algo1);
		painter.setBounds(0, 0, 2000, 2000);
		frame.getContentPane().add(painter);
		
		CPU painterCPU = new CPU(200,"painter"); // 60 FPS painter
		painterCPU.addFunction(frame::repaint);
		painterCPU.play();

		startTime = Instant.now();

		CPU timerPresent = new CPU(100 , "timerPresent");
		timerPresent.addFunction(this::presentTime);
		timerPresent.play();

		CPU returnHome = new CPU(100 , "returnHome");
		returnHome.addFunction(this::actionPerformed);
		returnHome.play();

		algo1.play();





		CPU updatesCPU = new CPU(60,"updates");
		updatesCPU.addFunction(algo1.drone::update);
		updatesCPU.play();
		
		CPU infoCPU = new CPU(10,"update_info");
		infoCPU.addFunction(this::updateInfo);
		infoCPU.play();
	}


	public void updateInfo(int deltaTime) {
		info_label.setText(algo1.drone.getInfoHTML());
		info_label2.setText("<html>" + String.valueOf(algo1.counter) + " <BR>isRisky:" + String.valueOf(algo1.is_risky) + 
				"<BR>" + String.valueOf(algo1.risky_dis) + "</html>");
		
	}

	private void presentTime(int stam)
	{
		Instant currentTime = Instant.now();
		Duration elapsedTime = Duration.between(startTime, currentTime);
		// Update the time label with the elapsed time
		long hours = elapsedTime.toHours();
		long minutes = elapsedTime.toMinutesPart();
		long seconds = elapsedTime.toSecondsPart();
		timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
	}
	private void actionPerformed(int stam)
	{
		Instant currentTime = Instant.now();
		Duration elapsedTime = Duration.between(startTime, currentTime);

		if (elapsedTime.getSeconds() > 25) {
			return_home = true;
			System.out.println(elapsedTime.getSeconds());
		}

		if(return_home) {
			algo1.speedDown();
			algo1.spinBy(180, true, new Func() {
				@Override
				public void method() {
					algo1.speedUp();
				}
			});
			while(true){
			}
		}
	}
	
	public void stopCPUS() {
		CPU.stopAllCPUS();
	}
	
	public void resumseCPUS() {
		CPU.stopAllCPUS();
	}
}
