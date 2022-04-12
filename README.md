# JsonToStaticJava
 Simple Helper Plugin

To use run messages:generate

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
		public static final String CHANGE_STORAGE_TYPE = "&4Please change your storage type to MYSQL or MARIADB & restart your server.";
		public static final String WARNING = "&cAre you sure you want to migrate? This action is irreversible.";
		public static final String BACKUP_HINT1 = "&cMake sure you have made a backup of your decks.yml before continuing.";
		public static final String BACKUP_HINT2 = "&cYou can easily backup all settings using /cards debug zip";
		public static final String CONFIRM_HINT = "&cIf you want to convert from YAML to";
		public static final String CONFIRM_CMD = "&cPlease type /cards migrate <deck|data> confirm";
	}
}
```

