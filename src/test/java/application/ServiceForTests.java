package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("SqlNoDataSourceInspection")
@Service
@Transactional
class ServiceForTests {
    private final JdbcTemplate template;

    @Autowired
    public ServiceForTests(JdbcTemplate template) {
        this.template = template;
    }

    void clearDB() {
        final String query = "TRUNCATE TABLE users CASCADE";
        template.update(query);
    }
}