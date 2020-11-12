CREATE EXTENSION pgcrypto;

CREATE TABLE Account (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  username TEXT NOT NULL,
  password TEXT NOT NULL,
  CONSTRAINT account_pk PRIMARY KEY (id),
  CONSTRAINT account_username_unique UNIQUE (username),
  CONSTRAINT account_username_length CHECK (char_length(username) > 0 AND char_length(username) <= 100)
);

CREATE TABLE Project (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  label TEXT NOT NULL,
  createdBy UUID NOT NULL,
  createdOn TIMESTAMP NOT NULL,
  CONSTRAINT project_pk PRIMARY KEY (id),
  CONSTRAINT project_label_unique UNIQUE (createdBy, label),
  CONSTRAINT project_label_length CHECK (char_length(label) > 0 AND char_length(label) <= 100),
  CONSTRAINT project_createdBy_fk FOREIGN KEY (createdBy) REFERENCES Account(id)
);

CREATE TABLE Description (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  label TEXT NOT NULL,
  CONSTRAINT description_pk PRIMARY KEY (id),
  CONSTRAINT description_label_unique UNIQUE (label),
  CONSTRAINT description_label_length CHECK (char_length(label) > 0 AND char_length(label) <= 100)
);

CREATE TABLE Category (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  descriptionId UUID NOT NULL,
  label TEXT NOT NULL,
  details TEXT NOT NULL,
  CONSTRAINT category_pk PRIMARY KEY (id, descriptionId),
  CONSTRAINT category_label_unique UNIQUE (label, descriptionId),
  CONSTRAINT category_label_length CHECK (char_length(label) > 0 AND char_length(label) <= 100),
  CONSTRAINT category_descriptionId_fk FOREIGN KEY (descriptionId) REFERENCES Description(id) ON DELETE CASCADE
);

CREATE TABLE Requirement (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  categoryId UUID NOT NULL,
  descriptionId UUID NOT NULL,
  label TEXT NOT NULL,
  details TEXT NOT NULL,
  CONSTRAINT requirement_pk PRIMARY KEY (id, categoryId, descriptionId),
  CONSTRAINT requirement_label_unique UNIQUE (label, categoryId),
  CONSTRAINT requirement_label_length CHECK (char_length(label) > 0 AND char_length(label) <= 100),
  CONSTRAINT requirement_categoryId_fk FOREIGN KEY (categoryId, descriptionId) REFERENCES Category(id, descriptionId) ON DELETE CASCADE
);

CREATE TABLE Test (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  requirementId UUID NOT NULL,
  categoryId UUID NOT NULL,
  descriptionId UUID NOT NULL,
  label TEXT NOT NULL,
  details TEXT NOT NULL,
  steps TEXT[] NOT NULL,
  CONSTRAINT test_pk PRIMARY KEY (id, requirementId, categoryId, descriptionId),
  CONSTRAINT test_label_unique UNIQUE (label, requirementId),
  CONSTRAINT test_label_length CHECK (char_length(label) > 0 AND char_length(label) <= 100),
  CONSTRAINT test_requirementId_fk FOREIGN KEY (requirementId, categoryId, descriptionId) REFERENCES Requirement(id, categoryId, descriptionId) ON DELETE CASCADE
);

CREATE TYPE AssessmentStatus AS ENUM ('OPEN', 'CLOSED');

CREATE TABLE Assessment (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  descriptionId UUID NOT NULL,
  projectId UUID NOT NULL,
  label TEXT NOT NULL,
  status AssessmentStatus NOT NULL DEFAULT 'OPEN',
  createdBy UUID NOT NULL,
  createdOn TIMESTAMP NOT NULL,
  lastModifiedBy UUID NOT NULL,
  lastModifiedOn TIMESTAMP NOT NULL,
  CONSTRAINT assessment_pk PRIMARY KEY (id),
  CONSTRAINT assessment_projectId_fk FOREIGN KEY (projectId) REFERENCES Project(id) ON DELETE CASCADE,
  CONSTRAINT assessment_descriptionId_fk FOREIGN KEY (descriptionId) REFERENCES Description(id) ON DELETE RESTRICT,
  CONSTRAINT assessment_createdBy_fk FOREIGN KEY (createdBy) REFERENCES Account(id),
  CONSTRAINT assessment_lastModifiedBy_fk FOREIGN KEY (lastModifiedBy) REFERENCES Account(id),
  CONSTRAINT assessment_label_length CHECK (char_length(label) > 0 AND char_length(label) <= 100),
  CONSTRAINT assessment_description_unique UNIQUE (id, descriptionId)
);

CREATE TYPE TestResultStatus AS ENUM ('SUCCESS', 'FAILURE');

CREATE TABLE TestResult (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  assessmentId UUID NOT NULL,
  testId UUID NOT NULL,
  requirementId UUID NOT NULL,
  categoryId UUID NOT NULL,
  descriptionId UUID NOT NULL,
  status TestResultStatus NOT NULL,
  CONSTRAINT testresult_pk PRIMARY KEY (id),
  CONSTRAINT testresult_assessment_fk FOREIGN KEY (assessmentId, descriptionId) REFERENCES Assessment(id, descriptionId) ON DELETE CASCADE,
  CONSTRAINT testresult_test_fk FOREIGN KEY (testId, requirementId, categoryId, descriptionId) REFERENCES Test(id, requirementId, categoryId, descriptionId) ON DELETE RESTRICT
);


