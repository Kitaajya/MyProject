drop  database if exists Shop;
create database if not exists Shop;
use Shop;
create table if not exists product(
    things int,                         -- 商品个数
    thingsName VARCHAR(40),             -- 商品名称
    thingsOrderID long,                 -- 商品单号
    purchaseIntention VARCHAR(40)       -- 用户意向
);
insert into product(things, thingsName, thingsOrderID, purchaseIntention) VALUES
           (2,'苹果',20250006,'香蕉'),
           (1,'橙子',20250007,'芒果'),
           (3,'橘子',20250008,'草莓');
select * from product;