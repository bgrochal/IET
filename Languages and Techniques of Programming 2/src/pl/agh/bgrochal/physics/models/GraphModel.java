package pl.agh.bgrochal.physics.models;

import org.jfree.chart.*;
import java.util.*;

import pl.agh.bgrochal.physics.views.*;

public class GraphModel {
	
	private double precision;
	private double minimum;
	private double maximum;
	private String graph;
	
	private String pathCSV;
	
	private MainApplicationView mainView;
	private GraphView graphView;
	private ParticleModel model;
	
	private JFreeChart secondChart;
	private Map<Double,Double> graphSeries = new TreeMap<Double,Double>();
	
	
	public GraphModel(ParticleModel model, GraphView graphView, MainApplicationView mainView) {
		this.graphView = graphView;
		this.mainView = mainView;
		this.model = model;
		
		this.precision = 0;
		this.minimum = -1;
		this.maximum = -1;
		this.graph = "";
		
		collectData();			/* Creates empty chart space */
	}
	
	public void collectData() {
		new Thread(new DataComputer(this, model, graphView)).start();
	}
	
	public void writeCSV() {
		new Thread(new WriterCSV(this, graphView, mainView)).start();
	}
	
	public Double getPrecision() {
		return this.precision;
	}
	
	public double getMinimum() {
		return this.minimum;
	}
	
	public double getMaximum() {
		return this.maximum;
	}
	
	public String getGraph() {
		return this.graph;
	}
	
	public String getPathCSV() {
		return this.pathCSV;
	}
	
	public Map<Double,Double> getGraphSeries() {
		return this.graphSeries;
	}
	
	public JFreeChart getSecondChart() {
		return this.secondChart;
	}
	
	public void setPrecison(double precision) {
		this.precision = precision;
	}
	
	public void setMinimum(double minimum) {
		this.minimum = minimum;
	}
	
	public void setMaximum(double maximum) {
		this.maximum = maximum;
	}
	
	public void setGraph(String graph) {
		this.graph = graph;
	}
	
	public void setPathCSV(String pathCSV) {
		this.pathCSV = pathCSV;
	}
	
	public void setGraphSeries(Map<Double,Double> graphSeries) {
		this.graphSeries = graphSeries;
	}
	
	public void setSecondChart(JFreeChart secondChart) {
		this.secondChart = secondChart;
	}
	
}
