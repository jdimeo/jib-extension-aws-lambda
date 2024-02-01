package com.github.jdimeo.jib;

import java.util.Map;
import java.util.Optional;

import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath;
import com.google.cloud.tools.jib.api.buildplan.ContainerBuildPlan;
import com.google.cloud.tools.jib.api.buildplan.FileEntriesLayer;
import com.google.cloud.tools.jib.api.buildplan.FileEntry;
import com.google.cloud.tools.jib.api.buildplan.LayerObject;
import com.google.cloud.tools.jib.maven.extension.JibMavenPluginExtension;
import com.google.cloud.tools.jib.maven.extension.MavenData;
import com.google.cloud.tools.jib.plugins.extension.ExtensionLogger;
import com.google.cloud.tools.jib.plugins.extension.ExtensionLogger.LogLevel;
import com.google.cloud.tools.jib.plugins.extension.JibPluginExtensionException;

public class LambdaExtension implements JibMavenPluginExtension<Configuration> {
	@SuppressWarnings("unchecked")
	private <L extends LayerObject> L modifyEntries(Configuration config, L layer) {
		var fileLayer = (FileEntriesLayer) layer;
		
		var entries = fileLayer.getEntries();
		entries.replaceAll(entry -> {
			var path = entry.getExtractionPath().toString();
			for (var r : config.getReplacements()) {
				path = path.replace(r.getSource(), r.getTarget());
			}
			return new FileEntry(entry.getSourceFile(),
				AbsoluteUnixPath.get(path), entry.getPermissions(), entry.getModificationTime(), entry.getOwnership());
		});
		
		return (L) fileLayer.toBuilder().setEntries(entries).build();
	}
	
	@Override
	public ContainerBuildPlan extendContainerBuildPlan(ContainerBuildPlan buildPlan, Map<String, String> properties,
			Optional<Configuration> extraConfig, MavenData mavenData, ExtensionLogger logger)
			throws JibPluginExtensionException {
		var config = extraConfig.orElseGet(Configuration::forAWSLambda);
		
		logger.log(LogLevel.LIFECYCLE, "Running AWS Lambda extension");
		
		var layers = buildPlan.getLayers();
		layers.replaceAll(layer -> modifyEntries(config, layer));
		return buildPlan.toBuilder().setLayers(layers).build();
	}
	
	@Override
	public Optional<Class<Configuration>> getExtraConfigType() {
		return Optional.of(Configuration.class);
	}
}
