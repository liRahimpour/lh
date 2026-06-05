CREATE TABLE customer (
                          id INT PRIMARY KEY,
                          name VARCHAR(255)
);

CREATE VIEW active_customers AS
SELECT * FROM customer;

CREATE PROCEDURE update_customer()
    LANGUAGE SQL
    AS $$
UPDATE customer SET name = 'Test';
$$;

CREATE FUNCTION calculate_score()
    RETURNS INT
AS $$
SELECT 1;
$$ LANGUAGE SQL;