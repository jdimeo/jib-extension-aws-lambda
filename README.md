# jib-extension-aws-lambda

This Google Jib plugin extension customizes the build plan for the convention that AWS Java Lambda expects.

By default, it relocates the following paths in the built image:

```
/apps/classes* → /var/task*
/apps/libs*    → /var/task/lib*
```

See the "Usage" tab of the [Lambda images that AWS provides](https://gallery.ecr.aws/lambda/java).

## Advanced Configuration

If you customize the `appRoot`, or want to relocate other files, you can use the configuration of this plugin to customize the path changes:

```xml
<configuration implementation="com.github.jdimeo.jib.Configuration">
	<replacements>
		<replace>
			<source>/app/resources/</source>
			<target>/var/resources/</target>
		</replace>
	</replacements>
</configuration>
```

## Usage

See [Jib's documentation](https://github.com/GoogleContainerTools/jib-extensions?tab=readme-ov-file#using-jib-plugin-extensions).

Specific to this extension, first add the JitPack repository as a plugin repository:

```xml
<pluginRepositories>
	<pluginRepository>
		<id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</pluginRepository>
</pluginRepositories>
```

Then register the extension with the Jib plugin:

```xml
<plugin>
	<groupId>com.google.cloud.tools</groupId>
	<artifactId>jib-maven-plugin</artifactId>
	<configuration>
		<pluginExtensions>
			<pluginExtension>
				<implementation>com.github.jdimeo.jib.LambdaExtension</implementation>
			</pluginExtension>
		</pluginExtensions>
	</configuration>
	<dependencies>
		<dependency>
			<groupId>com.github.jdimeo</groupId>
			<artifactId>jib-extension-aws-lambda</artifactId>
			<version>0.1.0</version>
		</dependency>
	</dependencies>
</plugin>
```