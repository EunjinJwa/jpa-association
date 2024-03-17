package persistence.entity.metadata;

public class RelationEntityTable {

    private final RelationType relationType;
    private final Class<?> entity;
    private final String joinColumnName;

    public RelationEntityTable(RelationType relationType, Class<?> entity, String joinColumnName) {
        this.relationType = relationType;
        this.entity = entity;
        this.joinColumnName = joinColumnName;
    }

    public RelationType getRelationType() {
        return relationType;
    }


    public Class<?> getEntity() {
        return entity;
    }

    public String getJoinColumnName() {
        return joinColumnName;
    }
}
