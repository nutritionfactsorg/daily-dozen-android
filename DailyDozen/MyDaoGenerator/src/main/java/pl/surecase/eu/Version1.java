package pl.surecase.eu;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Version1 extends SchemaVersion {

    public Version1(boolean current) {
        super(current);

        Schema schema = getSchema();
        addEntities(schema);
    }

    @Override
    public int getVersionNumber() {
        return 1;
    }

    private static void addEntities(Schema schema) {
        Entity user = addUserEntity(schema);

        Entity consumption = addConsumptionEntity(schema);
        Entity dailyReport = addDailyReportEntity(schema);

        // Add unidirectional to-many relationships
        addToManyRelation(dailyReport, consumption, "dailyReportId", "consumptions");

        addToManyRelation(user, dailyReport, "userId", "dailyReports");
    }

    private static Entity addDailyReportEntity(Schema schema) {
        Entity dailyOverview = schema.addEntity("DBDailyReport");

        dailyOverview.addIdProperty();
        dailyOverview.addDateProperty("date");

        return dailyOverview;
    }

    private static Entity addConsumptionEntity(Schema schema) {
        Entity consumption = schema.addEntity("DBConsumption");

        consumption.addIdProperty();
        consumption.addDoubleProperty("consumedServingCount");
        consumption.addStringProperty("foodTypeIdentifier");

        return consumption;
    }

    // if we want to support custom food types, then use this
    /*
    private static Entity addFoodTypeEntity(Schema schema) {
        Entity foodType = schema.addEntity("DBFoodType");

        foodType.addIdProperty();
        foodType.addStringProperty("name");
        foodType.addStringProperty("identifier");
        foodType.addDoubleProperty("recommendedDailyServingCount");
        foodType.addStringProperty("servingDescription");

        return foodType;
    }

    private static Entity addServingExampleEntity(Schema schema) {
        Entity servingExample = schema.addEntity("DBServingExample");

        servingExample.addIdProperty();
        servingExample.addStringProperty("title");
        servingExample.addStringProperty("example");

        return servingExample;
    }*/

    private static Entity addUserEntity(Schema schema) {
        Entity user = schema.addEntity("DBUser");

        user.addIdProperty();
        user.addLongProperty("usageCount");

        return user;
    }
}

