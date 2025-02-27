package persistence;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.core.DDLExcuteor;
import persistence.core.EntityManager;
import persistence.core.EntityManagerImpl;
import persistence.entity.Order;
import persistence.entity.OrderItem;
import persistence.entity.Person;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityManagerImplTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JdbcTemplate jdbcTemplate;
    private DDLExcuteor ddlExcuteor;

    private DatabaseServer server;
    EntityManager entityManager;

    @BeforeEach
    public void setUp() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlExcuteor = new DDLExcuteor(jdbcTemplate);
        entityManager = new EntityManagerImpl(server);

        createTable(Person.class);
        createTable(Order.class);
        createTable(OrderItem.class);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dropTable(Person.class);
        dropTable(Order.class);
        dropTable(OrderItem.class);
        server.stop();
    }

    private void createTable(Class<?> clazz) {
        ddlExcuteor.createTable(clazz);
    }

    private void dropTable(Class<?> clazz) {
        ddlExcuteor.dropTable(clazz);
    }


    @Test
    @DisplayName("find 실행")
    public void findTest() throws Exception {
        final Person person = new Person();
        person.setName("jinny");
        person.setAge(30);
        person.setEmail("test@gmail.com");

        entityManager.persist(person);

        Person savedPerson = entityManager.find(Person.class, 1L);

        assertThat(savedPerson).isNotNull();
    }

    @Test
    @DisplayName("entity 데이터 변경후 dirtyChecking에의한 update 실행")
    public void dirtyCheckFlushTest() {
        final Person person = new Person();
        person.setName("jinny");
        person.setAge(30);
        person.setEmail("test@gmail.com");

        entityManager.persist(person);
        Person savedPerson = entityManager.find(Person.class, 1L);
        savedPerson.setAge(33);

        entityManager.flush();

        Person updatedPerson = entityManager.find(Person.class, 1L);
        assertThat(updatedPerson.getAge()).isEqualTo(33);
    }

    @Test
    @DisplayName("entity 삭제")
    public void removeTest() {
        final Person person = new Person();
        person.setId(1L);
        person.setName("jinny");
        person.setAge(30);
        person.setEmail("");

        entityManager.persist(person);

        entityManager.remove(person);

        assertThatThrownBy(() -> entityManager.find(Person.class, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Entity not found");
    }

    @Test
    @DisplayName("entity relation 조회")
    public void findRelationEntityTest() {
        Order order = new Order();
        order.setOrderNumber("test1");

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct("phone");
        orderItem.setQuantity(2);
        order.setOrderItems(List.of(orderItem));

        entityManager.persist(order);

        Order persistedOrder = entityManager.find(Order.class, 1L);

        assertAll(
                () -> assertThat(persistedOrder).isNotNull(),
                () -> assertThat(persistedOrder.getOrderItems()).isNotEmpty(),
                () -> assertThat(persistedOrder.getOrderItems().get(0).getProduct()).isEqualTo("phone"),
                () -> assertThat(persistedOrder.getOrderItems().get(0).getQuantity()).isEqualTo(2)
        );

    }


}
