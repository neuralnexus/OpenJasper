-- sql script to set privileges on each table and sequence
-- Required for PostgreSQL 8
--

GRANT ALL PRIVILEGES ON TABLE account TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE agg_c_10_sales_fact_1997 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE agg_c_14_sales_fact_1997 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE agg_c_special_sales_fact_1997 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE agg_gender_ms_prodcat_sales_fact_1997 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE agg_l_03_sales_fact_1997 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE agg_l_04_sales_fact_1997 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE agg_l_05_sales_fact_1997 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE agg_lc_100_sales_fact_1997 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE agg_ll_01_sales_fact_1997 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE agg_pl_01_sales_fact_1997 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE category TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE currency TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE customer TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE customer_sales TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE days TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE department TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE employee TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE employee_closure TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE expense_fact TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE inventory_fact_1997 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE inventory_fact_1998 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE monthly_profit TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE position TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE product TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE product_class TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE product_sales TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE promotion TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE promotion_sales TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE region TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE reserve_employee TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE salary TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE sales_fact_1997 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE sales_fact_1998 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE sales_fact_dec_1998 TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE store TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE store_ragged TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE time_by_day TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE warehouse TO jasperdb;
GRANT ALL PRIVILEGES ON TABLE warehouse_class TO jasperdb;
