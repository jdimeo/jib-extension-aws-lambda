package com.github.jdimeo.jib;

import java.util.Map;
import java.util.Optional;

import com.google.auto.service.AutoService;
import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath;
import com.google.cloud.tools.jib.api.buildplan.ContainerBuildPlan;
import com.google.cloud.tools.jib.api.buildplan.FileEntriesLayer;
import com.google.cloud.tools.jib.api.buildplan.FileEntry;
import com.google.cloud.tools.jib.api.buildplan.LayerObject;
import com.google.cloud.tools.jib.maven.extension.JibMavenPluginExtension;
import com.google.cloud.tools.jib.maven.extension.MavenData;
import com.google.cloud.tools.jib.plugins.extension.ExtensionLogger;
import com.google.cloud.tools.jib.plugins.extension.JibPluginExtensionException;

@AutoService(JibMavenPluginExtension.class)
public class LambdaExtension implements JibMavenPluginExtension<Configuration> {
	private FileEntriesLayer modifyEntries(Configuration config, FileEntriesLayer layer) {
		var entries = layer.getEntries();
		entries.replaceAll(entry -> {
			var path = entry.getExtractionPath().toString();
			for (var r : config.getReplacements()) {
				path = path.replace(r.getSource(), r.getTarget());
			}
			return new FileEntry(entry.getSourceFile(),
				AbsoluteUnixPath.get(path), entry.getPermissions(), entry.getModificationTime(), entry.getOwnership());
		});
		
		return layer.toBuilder().setEntries(entries).build();
	}
	
	@Override
	public ContainerBuildPlan extendContainerBuildPlan(ContainerBuildPlan buildPlan, Map<String, String> properties,
			Optional<Configuration> extraConfig, MavenData mavenData, ExtensionLogger logger)
			throws JibPluginExtensionException {
		
		var config = extraConfig.orElseGet(Configuration::forAWSLambda);
		
		var layers = buildPlan.getLayers();
		layers.replaceAll(layer -> {
			if (layer instanceof FileEntriesLayer) {
				return cast(modifyEntries(config, (FileEntriesLayer) layer));
			}
			return layer;
		});
		return buildPlan.toBuilder().setLayers(layers).build();
	}
	
	@Override
	public Optional<Class<Configuration>> getExtraConfigType() {
		return Optional.of(Configuration.class);
	}
	
	@SuppressWarnings("unchecked")
	private static <L extends LayerObject> L cast(LayerObject layer) { return (L) layer; }
}
