package pl.agh.bgrochal.physics.models;

import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.util.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.axis.*;
import org.jfree.data.xy.*;
import org.jfree.chart.*;
import java.util.*;

import pl.agh.bgrochal.physics.views.*;

public class DataComputer implements Runnable {

	private GraphModel model;
	private GraphView graphView;
	private ParticleModel particle;
	private Map<Double,Double> graphSeries = new TreeMap<Double,Double>();
	
	public DataComputer(GraphModel model, ParticleModel particle, GraphView graphView) {
		this.graphView = graphView;
		this.particle = particle;
		this.model = model;
	}
	
	@Override
	public void run() {
		graphView.setProcessingLabel("Processing...");		/* !Model directly communicates with View when changes its state */
		graphView.getPrintCSVButton().setEnabled(false);
		graphView.getEnergyGraphButton().setEnabled(false);
		graphView.getChangeScaleButton().setEnabled(false);
		graphView.getVelocityGraphButton().setEnabled(false);
		
		if(model.getGraph().equals("velocityToEnergy"))
			countVelocityToEnergy();
		else
			countEnergyToVelocity();
		
		createChart();
		createLogChart();
		graphView.setProcessingLabel("");
		graphView.getPrintCSVButton().setEnabled(true);
		graphView.getEnergyGraphButton().setEnabled(true);
		graphView.getChangeScaleButton().setEnabled(true);
		graphView.getVelocityGraphButton().setEnabled(true);
		
		model.setGraphSeries(graphSeries);
	}
	
	public void countVelocityToEnergy() {
		double v = model.getMinimum();
		while(v < model.getMaximum()) {
			particle.setRelativeVelocity(v);
			particle.countEnergy();
			
			graphSeries.put(v, particle.getEnergy());			
			v += model.getPrecision();
		}
	}
	
	public void countEnergyToVelocity() {
		double E = model.getMinimum();
		while(E < model.getMaximum()) {
			particle.setEnergy(E);
			particle.countRelativeVelocity();
			
			graphSeries.put(E, particle.getRelativeVelocity());
			E += model.getPrecision();
		}
	}
	
	public void createChart() {
		/* Chart data */
		XYSeries data = new XYSeries("");
		for(Double key : graphSeries.keySet())
			data.add(key, graphSeries.get(key));
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(data);
		
		/* Creates chart */	
		JFreeChart newChart = ChartFactory.createXYLineChart(
				"",							/* Title */
				"", 						/* OX Axis Title */
				"", 						/* OY Axis Title */
				dataset,
				PlotOrientation.VERTICAL, 	/* Orientation */
				false, 						/* Legend */
				false, 						/* Tooltips */
				false						/* Generate URLs? */
			);
		
		graphView.setAppChart(newChart);	/* !Model directly communicates with View when changes its state */
	}
	
	public void createLogChart() {
		XYSeries data = new XYSeries("");
		for(Double key : graphSeries.keySet())
			data.add(key, graphSeries.get(key));
		
		LogAxis logAxis = new LogAxis("");
		logAxis.setSmallestValue(0.00001);
		logAxis.setBase(10);
		
		LogFormat format = new LogFormat(logAxis.getBase(), "", "", true);
		logAxis.setNumberFormatOverride(format);
		
		XYPlot plot;
		
		if(model.getGraph().equals("velocityToEnergy")) {
			plot = new XYPlot(
				new XYSeriesCollection(data),
				new NumberAxis(""),
				logAxis,
				new XYLineAndShapeRenderer(true, false)
			);
		}
		else {
			plot = new XYPlot(
				new XYSeriesCollection(data),
				logAxis,
				new NumberAxis(""),
				new XYLineAndShapeRenderer(true, false)
			);
		}
		
		JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
		model.setSecondChart(chart);
	}
	
}
