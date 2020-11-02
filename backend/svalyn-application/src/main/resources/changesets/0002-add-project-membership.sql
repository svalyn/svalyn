CREATE TABLE Project_Members (
  projectId UUID NOT NULL,
  accountId UUID NOT NULL,
  CONSTRAINT project_members_pk PRIMARY KEY (projectId, accountId),
  CONSTRAINT project_members_projectId_fk FOREIGN KEY (projectId) REFERENCES Project(id),
  CONSTRAINT project_members_accountId_fk FOREIGN KEY (accountId) REFERENCES Account(id)
);

ALTER TABLE Project ADD COLUMN ownedBy UUID NOT NULL;
ALTER TABLE Project ADD CONSTRAINT project_ownedBy_fk FOREIGN KEY (ownedBy) REFERENCES Account(id);