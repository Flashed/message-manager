DROP TABLE clients;
CREATE TABLE clients(
  id serial,
  PRIMARY KEY (id)
);

DROP TABLE messages;
CREATE TABLE messages (
  id serial,
  queueId int,
  senderId int,
  receiverId int,
  mess_text text,
  PRIMARY KEY (id)
);

DROP TABLE queues;
CREATE TABLE queues(
  id serial,
  PRIMARY KEY (id)
);