package pl.surecase.eu;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * Created by chankruse on 15-02-06.
 */
public abstract class SchemaVersion {
    public static final String CURRENT_SCHEMA_PACKAGE = "org.nutritionfacts.dailydozen.db";

    private final Schema schema;

    private final boolean current;

    public SchemaVersion(boolean current) {
        int version = getVersionNumber();
        String packageName = CURRENT_SCHEMA_PACKAGE;
        if (!current) {
            packageName += ".v" + version;
        }
        this.schema = new Schema(version, packageName);
        this.schema.enableKeepSectionsByDefault();
        this.current = current;
    }

    protected Schema getSchema() {
        return schema;
    }

    public boolean isCurrent() {
        return current;
    }

    public abstract int getVersionNumber();

    public static void addToOneRelation(Entity from, Entity to, String toId) {
        addToOneRelation(from, to, toId, to.getClassName().toLowerCase());
    }

    public static void addToOneRelation(Entity from, Entity to, String toId, String relationName) {
        Property toIdProperty = from.addLongProperty(toId).getProperty();
        from.addToOne(to, toIdProperty, relationName);
    }

    public static void addToManyRelation(Entity from, Entity to, String fromId, String relationName) {
        Property fromIdProperty = to.addLongProperty(fromId).getProperty();
        ToMany toMany = from.addToMany(to, fromIdProperty);
        toMany.setName(relationName);
    }
}

