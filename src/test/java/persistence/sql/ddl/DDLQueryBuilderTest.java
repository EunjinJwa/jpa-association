package persistence.sql.ddl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.core.EntityMetaManager;
import persistence.entity.Person;
import persistence.entity.Person1;
import persistence.entity.Person2;
import persistence.sql.ddl.h2.H2DDLQueryBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DDLQueryBuilderTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private DDLQueryBuilder ddlQueryBuilder = new H2DDLQueryBuilder();
    private final EntityMetaManager entityMetaManager = EntityMetaManager.getInstance();

    @BeforeEach
    public void beforeAll() {
        entityMetaManager.loadEntities();
    }

    @Test
    @DisplayName("CreateTableQuery_@Id 어노테이션은 Primary key 생성")
    void createTableQueryReq1() {
        String query = ddlQueryBuilder.createTableQuery(entityMetaManager.getEntityMetadata(Person1.class));

        assertEquals("CREATE TABLE person1 (id BIGINT PRIMARY KEY , name VARCHAR(255) , age INT )", query);
    }

    @Test
    @DisplayName("CreateTableQuery_@GeneratedValue 어노테이션은 Auto Increment 생성 및 @Column 컬럼명 생성")
    void createTableQueryReq2() {
        String query = ddlQueryBuilder.createTableQuery(entityMetaManager.getEntityMetadata(Person2.class));

        assertEquals("CREATE TABLE person2 (id BIGINT AUTO_INCREMENT PRIMARY KEY , nick_name VARCHAR(255) , old INT , email VARCHAR(255) NOT NULL )", query);
    }

    @Test
    @DisplayName("CreateTableQuery_@Transient제외")
    void createTableQueryReq3() {
        String query = ddlQueryBuilder.createTableQuery(entityMetaManager.getEntityMetadata(Person.class));

        assertEquals("CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY , nick_name VARCHAR(255) , old INT , email VARCHAR(255) NOT NULL )", query);
    }

    @Test
    @DisplayName("DropTableQuery")
    void dropTableQuery() {
        String query = ddlQueryBuilder.dropTableQuery(entityMetaManager.getEntityMetadata(Person.class));

        assertEquals("DROP TABLE users", query);
    }

}
