To help with various we should create a plugin (maven) that accepts json, and parses it into a static string data class.
For example: "internal-messages.json":

```json
"cmd-hint": "Test for note",
"migrate": {  
  "yaml-to-yaml": "&4Cannot convert from YAML to YAML.",  
 "change-storage-type": "&4Please change your storage type to MYSQL or MARIADB & restart your server.",  
 "warning": "&cAre you sure you want to migrate? This action is irreversible.",  
 "backup-hint1": "&cMake sure you have made a backup of your decks.yml before continuing.",  
 "backup-hint2": "&cYou can easily backup all settings using /cards debug zip",  
 "confirm-hint": "&cIf you want to convert from YAML to ",  
 "confirm-cmd": "&cPlease type /cards migrate <deck|data> confirm"  
}
```

Should turn into:

```java
public class InternalMessages {
	public static final String CMD_HINT = "Test for note";


	public static class Migrate {
		public static final String YAML_TO_YAML = "&4Cannot convert from YAML to YAML.";
		public static final String CHANGE_STORAGE_TYPE = "&4Please change your storage type to MYSQL or MARIADB & restart your server."
		public static final String WARNING = "&cAre you sure you want to migrate? This action is irreversible.";
		public static final String BACKUP_HINT1 = "&cMake sure you have made a backup of your decks.yml before continuing."
		public static final String BACKUP_HINT2 = "&cYou can easily backup all settings using /cards debug zip"
		public static final String CONFIRM_HINT = "&cIf you want to convert from YAML to";
		public static final String CONFIRM_CMD = "&cPlease type /cards migrate <deck|data> confirm";
	}
}
```

This should happen as a part of the build phase, or with a custom goal. So we can run
`mvn build` or `mvn jsonToJavaString:run` or something.

It should be seamless and work as a part of the build process as well so we can run ci/cd with it.

## Steps:
1. Should parse json files from select resources folder.
2. Should turn messages from name-name to camelcase java classes NameName.
3. TBD

### Custom Checkstyle
We should fork an existing checkstyle for this project.
We cannot have "root" duplicates.
We can have duplicates in childs. That is, not in the child object, but a child of child can be the same key in another child.
For example:

Compliant:
```json
"child": "",
"child1": { 
	"duplicate": "test1"
},
"child2": {
	"duplicate": "test2"
}
```

Non-compliant:
```json
"child": "test",
"child": { 
	"duplicate": "test1"
}
```
## After Generation:
We should format the file with something like https://code.revelc.net/formatter-maven-plugin/examples.html . We can use https://github.com/mojo-executor/mojo-executor to execute that plugin from within ours.

## Configuration:
We should accept a source folder & target folder. Possibly should accept a target package instead of target folder.
```xml
<configuration>
	<overwriteClasses>true</overwriteClasses>
	<sourcefolder>${project.basedir}/src/main/resources/internal</sourcefolder>
	<targetpackage>com.example.internal</targetpackage>
</configuration>
```

All Strings should be public static final.

# design doc for paths
The messages generation is used to grab default values from config.yml files as well as generate messages used internally.

We should be able to get the path of those as well. So generating and grabbing values will be

```json
{
  "cmd-hint": "Test for note",
  "migrate": {
    "yaml-to-yaml": "&4Cannot convert from YAML to YAML."
  }
}
```

Should turn to:

```java
public class InternalMessages {
    public static final ConfigMessageType CMD_HINT = new ConfigMessageType("cmd-hint", "Test for note");
    public static class Migrate {
        public static final ConfigMessageType YAML_TO_YAML = new ConfigMessageType("migrate.yaml-to-yaml","&4Cannot convert from YAML to YAML.");
    }
}
```
This record should generate somewhere once.
```java
public record ConfigMessageType (final String path, final String value){
    @Override
    public String toString() {
        return value;
    }
}
```

This way we can handle paths changing easily as well.
