DROP TABLE clients CASCADE;
CREATE TABLE clients(
  id serial,
  PRIMARY KEY (id)
);

DROP TABLE messages;
CREATE TABLE messages (
  id serial,
  queueId int NOT NULL,
  senderId int NOT NULL,
  receiverId int,
  mess_text text,
  PRIMARY KEY (id),
  FOREIGN KEY (queueId) REFERENCES queues (id) ON DELETE CASCADE,
  FOREIGN KEY (senderId) REFERENCES clients (id),
  FOREIGN KEY (receiverId) REFERENCES clients (id)
);

DROP TABLE queues CASCADE;
CREATE TABLE queues(
  id serial,
  PRIMARY KEY (id)
);