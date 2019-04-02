package fr.irit.smac.mas;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import Jama.Matrix;
import be.cetic.tsimulus.Utils;
import be.cetic.tsimulus.config.Configuration;
import be.cetic.tsimulus.config.GeneratorFormat;
import be.cetic.tsimulus.generators.Generator;
import be.cetic.tsimulus.timeseries.TimeSeries;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.mas.EnvironmentF.Expe;
import scala.Function1;
import scala.io.Source;
import spray.json.JsValue;
import be.cetic.tsimulus.config.Configuration;
import com.github.nscala_time.time.Imports.*;
import spray.json.*;
public class Tests {

	public static void main(String[] args) {

		AmasF amas = new AmasF(new EnvironmentF(Scheduling.DEFAULT, args, Expe.RANDOM), Scheduling.DEFAULT, args);
		/*Generator<Double> gen = new Generator<Double>(null, null) {

			@Override
			public TimeSeries<Double> timeseries(Function1<String, Generator<Object>> generators) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public JsValue toJson() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		TimeSeries<Double> bla;
	
		String content = Source.fromFile(new File("tsimules_test.json"), "bla").getLines().mkString("\n");
		DateTimeFormatter val = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSS");
		
		Configuration(content.par)
		
		Utils.generate(arg0)
		
		String content = Source .fromFile(new File("tsimules_test.json")).getLines().mkString("\n");

		DateTimeFormatter dtf = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSS");
		
		Configuration config = Configuration(content.parseJson);
		
		println("date;series;value")
		
		Utils.generate(Utils.config2Results(config)) foreach (e => println(dtf.print(e._1) + ";" + e._2 + ";" + e._3));*/
	}

}
