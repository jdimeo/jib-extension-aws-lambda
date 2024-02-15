package com.github.jdimeo.jib;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
	public static class Replace {
		private String source, target;
		
		public Replace() {
			// For serialization
		}
		private Replace(String source, String target) {
			this.source = source;
			this.target = target;
		}
		
		public String getSource() {
			return source;
		}
		public String getTarget() {
			return target;
		}
	}

	private List<Replace> replacements = new ArrayList<>();
	
	public List<Replace> getReplacements() {
		return replacements;
	}
	
	public static Configuration forAWSLambda() {
		var ret = new Configuration();
		ret.getReplacements().add(new Replace("/app/classes", "/var/task"));
		ret.getReplacements().add(new Replace("/app/resources", "/var/task"));
		ret.getReplacements().add(new Replace("/app/libs", "/var/task/lib"));
		return ret;
	}
}
