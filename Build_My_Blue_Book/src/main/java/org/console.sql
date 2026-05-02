create database if not exists LogInBlueBook;
use LogInBlueBook;
drop table if exists logInOfInformation;
create table if not exists logInOfInformation(
    name varchar(10),     /*用户本名*/
    webName varchar(10),  /*用户网名*/
    selfId varchar(18)     /*身份证号*/
);
drop table if exists checkAndJustify;
create table if not exists checkAndJustify(
          phoneNumber bigint(11) primary key,/*手机号*/
          checkingNumber int(4)not null  ,    /*验证码*/
          effectTime timestamp default current_timestamp/*有效时间*/
);
insert into checkAndJustify(phoneNumber, checkingNumber) values (15175601727,
                                                               floor(rand() * 10000));
select checkingNumber from checkAndJustify;
/*删除超时的验证码*/
delete from checkAndJustify where timestampdiff(second,effectTime,now())>=60;


INSERT INTO logInOfInformation (name, webName, selfId) VALUES
                        ('张伟',null,'110101199503124518'),
                        ('李娜',null,'310105199807213629'),
                        ('王浩',null,'440306200011057836'),
                        ('刘婷',null,'510104199902189257'),
                        ('陈明',null,'330103199709091462'),
                        ('杨雨',null,'610102200106305713'),
                        ('赵宇',null,'320106199604158925'),
                        ('黄欣',null,'420107199908226341'),
                        ('周杰',null,'530105200201194876'),
                        ('吴悦',null,'120104199805273594');
