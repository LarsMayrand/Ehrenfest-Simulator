import java.awt.*;
import java.util.Random;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.FontMetrics;
import java.lang.Thread;

public class BallPanel extends Canvas {

	// Rendering / Buffer objects
	private BufferStrategy strategy;
	private Graphics2D g2;

	// Ball objects
	private Ball[] balls = new Ball[5000];
	private Ball currentBall;
	private int ballCount;

	// Power Arrow
	private Arrow powerArrow;
	private float arrowScale = 5.0f;

	// Frames
	private int maxFrameRate = 66;
	private int currentFrameRate;

	// Ehrenfest terms
	private double x = 0; // Number of balls in urn 0
	private double M = 0; // Total balls
	private int steps = 0;
	
//	g2.drawRoundRect(190, 170, 200, 200, 25, 25);
//	g2.drawRoundRect(410, 170, 200, 200, 25, 25);
	
	public void urn() {
		int selected = (int) (StdRandom.uniform((int) M));
		//System.out.println(++steps);
		//System.out.println(selected + ", " + balls[selected].urn());
		if (balls[selected].urn()) { // if randomly slect
			balls[selected].position.setX(420 + StdRandom.uniform(130)); 
			balls[selected].position.setY(180);
			x--;
		} else {
			balls[selected].position.setX(200 + StdRandom.uniform(130));
			balls[selected].position.setY(180);
			x++;
		}
	}
	
	public double findX() {
		int counter = 0;
		for (int i = 0; i < M; i++) {
			if (balls[i].urn()) {
				counter++;
			}
		}
		return counter; 
	}
	
	public BallPanel()
	{
		setPreferredSize(new Dimension(800, 600));
		setIgnoreRepaint(true);

		// Wire up Events
		MouseHandler mouseHandler = new MouseHandler();
		addMouseMotionListener(mouseHandler);
		addMouseListener(mouseHandler);
	}

	// Start Render and Update Threads
	public void start()
	{
		mainLoop();
	}

	public void mainLoop()
	{
		long previousTime = System.currentTimeMillis();
		long currentTime = previousTime;
		long elapsedTime;
		long totalElapsedTime = 0;
		int frameCount = 0;
		
		while(true)
		{
			currentTime = System.currentTimeMillis();
			elapsedTime = (currentTime - previousTime); // elapsed time in seconds
			totalElapsedTime += elapsedTime;

			if (totalElapsedTime > 1000)
			{
				currentFrameRate = frameCount;
				frameCount = 0;
				totalElapsedTime = 0;
			}

			updateGame(elapsedTime / 1000f);
			render();

			try
			{
				//Thread.sleep(getFpsDelay(maxFrameRate));
				Thread.sleep(5);
			} catch (Exception e) {
				e.printStackTrace();
			}

			previousTime = currentTime;
			frameCount++;

		}

	}

	private int getFpsDelay(int desiredFps)
	{
		return 1000 / desiredFps;
	}


	public void clearBalls()
	{
		ballCount = 0;
		x = 0;
		M = 0;
	}

	public void setGravity(float pixelsPerSecond)
	{
		Constants.gravity = pixelsPerSecond;
	}

	public void generateBalls(int numBalls)
	{
		Random rand = new Random();
		for (int i = 0; i < numBalls; i++)
		{
			Ball tempBall = new Ball(rand.nextInt(10) + getWidth()/2, rand.nextInt(10) + getHeight()/2, 10, .1f, 0);
		//	tempBall.velocity.set((rand.nextFloat() * 4000) - 2000, (rand.nextFloat() * 4000) - 2000);
		   balls[ballCount] = tempBall;
		   ballCount++;
		}
	}

	public void scatterBalls()
	{
		Random rand = new Random();
		for (int i = 0; i < this.ballCount; i++)
		{
			balls[i].velocity.set((rand.nextFloat() * 3000) - 1500, (rand.nextFloat() * 3000) - 1500);
		}
	}

	public void render()
	{
		//System.out.printf("Width: %d Height: %d\n", getWidth(), getHeight());
		
		if (strategy == null || strategy.contentsLost())
		{
			// Create BufferStrategy for rendering/drawing
			createBufferStrategy(2);
			strategy = getBufferStrategy();
			Graphics g = strategy.getDrawGraphics();
			g2 = (Graphics2D) g;
		}

		// Turn on anti-aliasing
		g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Render Background
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, getWidth(), getHeight());

		// Urn outline
		g2.setColor(Color.WHITE);
		g2.drawRoundRect(190, 170, 200, 200, 25, 25);
		g2.drawRoundRect(410, 170, 200, 200, 25, 25);
		
		// Number line for reduced model
		g2.drawLine(100, 480, 700, 480); // line length = 600
		g2.fillOval(99, 475, 10, 10);
		g2.fillOval(694, 475, 10, 10);
		for (int i = 1; i < M; i++) {
			g2.drawLine( (int) (100 + i * 600 / M), 470, (int) (100 + i * 600 / M), 490);
			g2.drawString(i + "", (int) (100 + i * 600 / M), 530);
		}
		g2.drawString(0 + "", 99, 530);
		g2.drawString("M", 701, 530);
		
		
		// Mark current state in reduced Ehrenfest model
		if (M > 1) {
			x = findX();
			g2.setColor(Color.YELLOW);
			g2.fillOval((int) (93 + 600 / M * x), 473, 15, 15);
			//System.out.println((int) (93 + 600 / M * x));
		}
		
		// Erase top of Urn
		g2.setColor(Color.BLACK);
		g2.fillRect(170, 130, 600, 50);
		
		// Render Game Objects
		for(int i = 0; i < ballCount; i++)
		{
			balls[i].draw(g2);
		}
		
		
		Ball tempBall = currentBall;
		if (tempBall != null) tempBall.draw(g2);

		// Render Foreground (text, etc)

  		// Draw Power Arrow and Speed Text along arrow if we are launching a ball
  		Arrow tempArrow = powerArrow;
  		if (tempArrow != null)
  		{
  			tempArrow.draw(g2);

  			// Power Arrow Magnitude Text
  			g2.setColor(Color.WHITE);
  			g2.drawString(String.format("%.2f px/s", tempArrow.getLength() * arrowScale, 2), (tempArrow.getX2() + tempArrow.getX1())/2, (tempArrow.getY1() + tempArrow.getY2())/2);
  		}

  		// Display Help Text in center if no balls
//  		if (ballCount == 0 && currentBall == null)
//  		{
//  			String helpString = "Click and drag your mouse to launch a ball.";
//  			g2.setColor(Color.WHITE);
//  			g2.drawString(helpString,  getWidth()/2 - (g2.getFontMetrics().stringWidth(helpString)/2), getHeight()/2);
//  		}

  		// Draw our framerate and ball count
  		g2.setColor(Color.WHITE);
  		g2.drawString("FPS: " + currentFrameRate + " Balls: "  + ballCount, 15, 15);


  		if (!strategy.contentsLost()) strategy.show();

	}

	public void updateGame(float elapsedSeconds)
	{


		// step the position of movable objects based off their velocity/gravity and elapsedTime
		for (int i = 0; i < ballCount; i++)
		{
			balls[i].velocity.setY(balls[i].velocity.getY() + (Constants.gravity * (elapsedSeconds)));

			balls[i].position.setX(balls[i].position.getX() + (balls[i].velocity.getX() * (elapsedSeconds)));
			balls[i].position.setY(balls[i].position.getY() + (balls[i].velocity.getY() * (elapsedSeconds)));

			// TODO - Where should I be checking for epsilon?
			if (Math.abs(balls[i].velocity.getX()) < Constants.epsilon) balls[i].velocity.setX(0);
			if (Math.abs(balls[i].velocity.getY()) < Constants.epsilon) balls[i].velocity.setY(0);

		}

		checkCollisions();

	}

	// Insertion sort for Sweep and Prune
	public void insertionSort(Comparable[] a)
	{
		 for( int p = 1; p < ballCount; p++ )
	     {
	         Comparable tmp = a[ p ];
	         int j = p;

	         for( ; j > 0 && tmp.compareTo( a[ j - 1 ] ) < 0; j-- )
	             a[ j ] = a[ j - 1 ];

	         a[ j ] = tmp;
		 }
	}


	public void checkCollisions()
	{
		insertionSort(balls);

		// Check for collision with walls
		for (int i = 0; i < ballCount; i++)
		{
		//	System.out.println("Ball #" + i + ": " + (balls[i].position.getX() - balls[i].getRadius()));
		
			if (balls[i].position.getY() > 170 && balls[i].position.getY() < 370) {
				// Left Wall, Left Urn
				if (balls[i].position.getX() - balls[i].getRadius() > 185 && balls[i].position.getX() - balls[i].getRadius() < 189)
				{
					balls[i].position.setX(190 + balls[i].getRadius()); 
					balls[i].velocity.setX(-(balls[i].velocity.getX() * Constants.restitution)); 
					balls[i].velocity.setY(balls[i].velocity.getY() * Constants.restitution);
				}
			
				// Left Wall, Right Urn
				if (balls[i].position.getX() - balls[i].getRadius() > 405 && balls[i].position.getX() - balls[i].getRadius() < 410)
				{
					balls[i].position.setX(410 + balls[i].getRadius()); 
					balls[i].velocity.setX(-(balls[i].velocity.getX() * Constants.restitution)); 
					balls[i].velocity.setY(balls[i].velocity.getY() * Constants.restitution);
				}
			
//			g2.drawRoundRect(190, 170, 200, 200, 25, 25);
//			g2.drawRoundRect(410, 170, 200, 200, 25, 25);
			
				// Right Wall, Left Urn
				if (balls[i].position.getX() + balls[i].getRadius() > 390 && balls[i].position.getX() + balls[i].getRadius() < 395)
				{
					balls[i].position.setX(390 - balls[i].getRadius());	
					balls[i].velocity.setX(-(balls[i].velocity.getX() * Constants.restitution));
					balls[i].velocity.setY((balls[i].velocity.getY() * Constants.restitution));
				}
				
				// Right Wall, Right Urn
				if (balls[i].position.getX() + balls[i].getRadius() > 610 && balls[i].position.getX() + balls[i].getRadius() < 615)
				{
					balls[i].position.setX(610 - balls[i].getRadius());		// Place ball against edge
					balls[i].velocity.setX(-(balls[i].velocity.getX() * Constants.restitution)); // Reverse direction and account for friction
					balls[i].velocity.setY((balls[i].velocity.getY() * Constants.restitution));
				}
			
			}
			
			// Left Wall
			if (balls[i].position.getX() - balls[i].getRadius() < 0)
			{
				balls[i].position.setX(balls[i].getRadius()); // Place ball against edge
				balls[i].velocity.setX(-(balls[i].velocity.getX() * Constants.restitution)); // Reverse direction and account for friction
				balls[i].velocity.setY(balls[i].velocity.getY() * Constants.restitution);
			}
			else if (balls[i].position.getX() + balls[i].getRadius() > getWidth()) // Right Wall
			{
				balls[i].position.setX(getWidth() - balls[i].getRadius());		// Place ball against edge
				balls[i].velocity.setX(-(balls[i].velocity.getX() * Constants.restitution)); // Reverse direction and account for friction
				balls[i].velocity.setY((balls[i].velocity.getY() * Constants.restitution));
			}

			if (balls[i].position.getY() - balls[i].getRadius() < 0)				// Top Wall
			{
				balls[i].position.setY(balls[i].getRadius());				// Place ball against edge
				balls[i].velocity.setY(-(balls[i].velocity.getY() * Constants.restitution)); // Reverse direction and account for friction
				balls[i].velocity.setX((balls[i].velocity.getX() * Constants.restitution));
			}
			if (balls[i].position.getY() + balls[i].getRadius() > getHeight()) // Bottom Wall
			{
				balls[i].position.setY(getHeight() - balls[i].getRadius());		
				balls[i].velocity.setY(-(balls[i].velocity.getY() * Constants.restitution));    
				balls[i].velocity.setX((balls[i].velocity.getX() * Constants.restitution));
			}
			
			// Bottoms of Urns
			if (balls[i].position.getY() + balls[i].getRadius() > 370 && balls[i].position.getY() + balls[i].getRadius() < 383)
			{
				if (balls[i].position.getX() + balls[i].getRadius() < 614 && balls[i].position.getX() + balls[i].getRadius() > 189) {
					balls[i].position.setY(370 - balls[i].getRadius());		
					balls[i].velocity.setY(-(balls[i].velocity.getY() * Constants.restitution));    
					balls[i].velocity.setX((balls[i].velocity.getX() * Constants.restitution));
				}
			}
			
			// Ball to Ball collision
			for(int j = i + 1; j < ballCount; j++)
			{
				if ((balls[i].position.getX() + balls[i].getRadius()) < (balls[j].position.getX() - balls[j].getRadius()))
						break;

				if((balls[i].position.getY() + balls[i].getRadius()) < (balls[j].position.getY() - balls[j].getRadius()) ||
				   (balls[j].position.getY() + balls[j].getRadius()) < (balls[i].position.getY() - balls[i].getRadius()))
				   		continue;

				balls[i].resolveCollision(balls[j]);

			}
		}

	}
	
	private class MouseHandler extends MouseAdapter implements MouseMotionListener
	{
		public void mousePressed(MouseEvent e)
	   	{
			//Random randGen = new Random();
			//int randSize = randGen.nextInt(30) + 10;

			// DO STUFF HERE TO ADD BALL DRAGGING
//			if (e.getX() == )
//			for (Ball ball : balls) {
//				
//			}
			currentBall = new Ball(e.getX(), e.getY(), 15, 15, ballCount);
			powerArrow = new Arrow(e.getX(), e.getY(), e.getX(), e.getY());
	   	}

	   public void mouseReleased(MouseEvent e)
	   {
		   // Change in x/y per second
		   float xVector = (powerArrow.getX2() - powerArrow.getX1()) * 5;
		   float yVector = (powerArrow.getY2() - powerArrow.getY1()) * 5;

		   currentBall.velocity.set(xVector, yVector);
		   balls[ballCount] = currentBall;
		   ballCount++;
		   M++;

		   currentBall = null;
		   powerArrow = null;
	   }

	   public void mouseDragged(MouseEvent e)
	   {
			int x1 = powerArrow.getX1();
			int y1 = powerArrow.getY1();
			int x2 = e.getX();
			int y2 = e.getY();
			int dx = Math.abs(x2 - x1);
			int dy = Math.abs(y2 - y1);

			if ((x2 - x1) < 0)
			{
				powerArrow.setX2(x1 + dx);
			}
			else
			{
				powerArrow.setX2(x1 - dx);
			}

			if ((y2 - y1) < 0)
			{
				powerArrow.setY2(y1 + dy);
			}
			else
			{
				powerArrow.setY2(y1 - dy);
			}
	   }

	   public void mouseMoved(MouseEvent e)
	   {
		   // Nada
	   }
  }


}
