DROP TABLE clients;
CREATE TABLE clients(
  id int,
  PRIMARY KEY (id)
);

DROP TABLE messages;
CREATE TABLE messages (
  id int,
  queueId int,
  senderId int,
  receiverId int,
  mess_text text,
  PRIMARY KEY (id)
);

DROP TABLE queues;
CREATE TABLE queues(
  id int,
  PRIMARY KEY (id)
);