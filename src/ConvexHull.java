import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;
import java.util.Comparator;
import static java.lang.Math.abs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.WHITE;
import javafx.stage.Stage;

/**
 * QUICK HULL WITH DIVIDE AN CONQUER ALGORITHM
 * @author ERMA SAFIRA NURMASYITA
 * 13516072
 *
 */
public class ConvexHull extends Application {
    ArrayList<Point> arrayPoint;
    static int Neff;
    Canvas canvas;
    int width = 600;
    int height = 600;
    GraphicsContext gc;
    
    /**
     * Start a Canvas and call the Convex Hull Algorithm
     */
    @Override
    public void start(Stage stage) throws Exception {
        //START CANVAS
        Group root = new Group();
	canvas = new Canvas(width-30, height-30);
	gc = canvas.getGraphicsContext2D();
	canvas.setTranslateX(15);
	canvas.setTranslateY(15);
	root.getChildren().add(canvas);
	
        //FORM CONVEX HULL
        ArrayList<Line2D> arrLine = new ArrayList<Line2D>();
        createRandomPoints(Neff);
        double start = System.nanoTime();  
	arrLine = formConvexHull();
        double elapsedTime = System.nanoTime() - start;
        
        /*To avoid line duplication*/
	if (arrLine.size() == 2) {
            arrLine.remove(1);
        }
        
        //DRAW AND OUTPUTS
        System.out.println("Time elapsed = " + elapsedTime/1000000 + " ms");
        System.out.println("Number of side(s) = " + arrLine.size());
        PrintLine(arrLine);
        draw(gc, arrLine);
	stage.setScene(new Scene(root, width, height));
	stage.show();
	stage.setTitle("Convex Hull");
    }
    /**
     * Form Convex Hull
     * @return array of Convex Hull Line(s)
     */
    public ArrayList<Line2D> formConvexHull() {
        ArrayList<Line2D> arrLine = new ArrayList<>();
        //SORT POINTS
        Collections.sort(arrayPoint, new Comparator<Point>() {
        public int compare(Point p1, Point p2) {
            if (p1.x == p2.x) {
                return Integer.compare(p1.y, p2.y);
            } else {
                return Integer.compare(p1.x, p2.x);
            }
        }
        });
        Point Pmin = arrayPoint.get(0);
        Point Pmax = arrayPoint.get(Neff-1);
        convexHullDAC("L", Pmin, Pmax, arrayPoint, arrLine);
        convexHullDAC("R", Pmin, Pmax, arrayPoint, arrLine);
        return arrLine;
    }
    /**
     * Divide and Conquer Strategy for Convex Hull
     * @param orient = Left("L") or Right("R")
     * @param P1
     * @param Pn
     * @param arrPoint
     * @param listLine
     */
    public void convexHullDAC(String orient, Point P1, Point Pn, ArrayList<Point> arrPoint, ArrayList<Line2D> listLine) {
        ArrayList<Point> pointsLeft = new ArrayList<>();
        if ("L".equals(orient)) {
            pointsLeft = enumLeftPoints(P1, Pn, arrPoint);
        } else {
            pointsLeft = enumRightPoints(P1, Pn, arrPoint);
        }
        
        if (!pointsLeft.isEmpty()) {
            //DIVIDE
            Point Pmax = new Point();
            Pmax = maxPoint(P1, Pn, pointsLeft);
            convexHullDAC(orient, P1, Pmax, pointsLeft, listLine);
            convexHullDAC(orient, Pmax, Pn, pointsLeft, listLine);
        } else {
            //CONQUER
            Line2D.Double L;
            L = new Line2D.Double(P1, Pn);
            listLine.add(L);
        }
    }
    /**
     * Check the location of P3 from line P1P2
     * @param P1: initial point              | x1 y1 1 |
     * @param P2: final point          det = | x2 y2 1 |
     * @param P3: the point we check         | x3 y3 1 |
     * @return det
     */
    int pointDeterminant(Point P1, Point P2, Point P3) {
        int det = P1.x*P2.y + P3.x*P1.y + P2.x*P3.y - P3.x*P2.y - P2.x*P1.y - P1.x*P3.y;
        return det;
    }
    /**
     * Enumerate Points on the leftside/rightside of line PminPmax
     * @param Pmin
     * @param Pmax
     * @param arrPoint
     * @return array of points on the lefside/rightside
     */
    ArrayList<Point> enumLeftPoints(Point Pmin, Point Pmax, ArrayList<Point> arrPoint) {
        ArrayList<Point> leftPoints = new ArrayList<>();
        for (int i=0; i<arrPoint.size(); i++) {
            Point P = arrPoint.get(i);
            if (pointDeterminant(Pmin, Pmax, P) < 0)
                leftPoints.add(P);
        }
        return leftPoints;
    }
    ArrayList<Point> enumRightPoints(Point Pmin, Point Pmax, ArrayList<Point> arrPoint) {
        ArrayList<Point> rightPoints = new ArrayList<>();
        for (int i=0; i<arrPoint.size(); i++) {
            Point P = arrPoint.get(i);
            if (pointDeterminant(Pmin, Pmax, P) > 0)
                rightPoints.add(P);
        }
        return rightPoints;
    }
    /**
     * Area of a triangle if the coordinates of the three vertices are given
     * @param P1                   | x1 x2 x3 |
     * @param P2    A = 1/2 * abs( | y1 y2 y3 | )
     * @param P3                   | 1  1  1  |
     * @return Area of triangle.
     */
    public double TriangleArea(Point P1, Point P2, Point P3) {
        return abs(P1.x*P2.y + P2.x*P3.y + P3.x*P1.y - P1.x*P3.y - P3.x*P2.y - P2.x*P1.y)/2;
    }
    /** 
     * Search for the furthest point from P1P2
     * @param P1
     * @param P2
     * @param arrPoint: array of Points
     * @return Pmax
     */
    public Point maxPoint(Point P1, Point P2, ArrayList<Point> arrPoint) {
        double maxArea = 0;
        double currArea;
        Point Pmax = new Point();
        for (int i=0; i<arrPoint.size(); i++) {
            Point P = arrPoint.get(i);
            currArea = TriangleArea(P1, P2, P);
            if (currArea > maxArea) {
                Pmax = P;
                maxArea = currArea;
            }
        }
        return Pmax;
    }
    /**
     * Print Line P1P2
     * @param arrLine 
     */
    public void PrintLine(ArrayList<Line2D> arrLine) {
        for (int i=0; i<arrLine.size(); i++) {
            System.out.print(i+1 + " [(" + arrLine.get(i).getP1().getX() + ",");
            System.out.print(arrLine.get(i).getP1().getY() + "), (");
            System.out.print(arrLine.get(i).getP2().getX() + ",");
            System.out.println(arrLine.get(i).getP2().getY() + ")]");
        }
    }
    /**
     * Randomize N points
     * @param N
     */
    public void createRandomPoints(int N)
    {
        arrayPoint = new ArrayList<>();
	int width = ((int) canvas.getWidth()) - 30;
	int height = ((int) canvas.getHeight()) - 30;
	for (int i = 0; i < N; i++) {
            int c = ((int) (Math.random() * (width)));
            int d = ((int) (Math.random() * (height)));
            if (c +15 < width)
		c += 15;
            if (d + 15 < height)
		d += 15;
            Point p = new Point(c, d);
            arrayPoint.add(p);
	}
    }
    /**
     * Draw to points and Convex Hull on scene
     * @param gc: GraphicContent
     * @param listLine 
     */
    public void draw(GraphicsContext gc, ArrayList<Line2D> listLine) {
        gc.setFill(Color.BLACK);
        //DRAW POINT
	gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	gc.setStroke(Color.BLACK);
	gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
	gc.setFill(Color.RED);
	for (int i = 0; i < arrayPoint.size(); i++) {
            gc.fillOval(arrayPoint.get(i).x -2, arrayPoint.get(i).y-2, 5, 5);
	}
        //DRAW LINE
        double x, y;
	for (int i = 0; i < listLine.size(); i++) {
            gc.setStroke(WHITE);
            gc.beginPath();
            gc.setLineWidth(2);
            x = listLine.get(i).getP1().getX();
            y = listLine.get(i).getP1().getY();
            gc.moveTo(x, y);
            gc.stroke();
            x = listLine.get(i).getP2().getX();
            y = listLine.get(i).getP2().getY();
            gc.lineTo(x, y);
            gc.stroke();
	}
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.print("Enter a number of points: ");
	Neff = reader.nextInt();
        reader.close();
        if (Neff>1)
            launch(args);
        else {
            System.out.println("Can't form Convex Hull Polygon using less than 2 points.");
            Platform.exit();
        }
    }
}