drop table if exists Tag;
drop table if exists Illness;
drop table if exists Medication;
drop table if exists PatientData;
drop table if exists MedicationToPatient;
drop table if exists ExaminationResults;
drop table if exists MedicationToExaminationResults;
drop table if exists ExaminationResultsToTag;
drop table if exists FishExamResults;
drop table if exists CariotypeExamResults;

create table Tag (
  id int not null primary key auto_increment,
  name nvarchar(255) not null,
  
  unique index ix_tag_name(name)
);

create table Illness (
  id int not null primary key auto_increment,
  name nvarchar(255) not null,
  
  unique index ix_illness_name(name)
);

create table Medication (
  id int not null primary key auto_increment,
  name nvarchar(255) not null,
  illnessId int not null,
  
  unique index ix_medication_illnessid_name(illnessId, name),
  index ix_medication_illnessid(illnessId),
  index ix_medication_name(name)
);

create table PatientData (
  id int not null primary key auto_increment,
  lastName nvarchar(255) not null,
  firstName nvarchar(255) null,
  patronymicName nvarchar(255) null,
  address mediumtext,
  birthDateYear int,
  birthDateMonth int,
  birthDateDay int,
  diagnosisDateYear int,
  diagnosisDateMonth int,
  diagnosisDateDay int,
  deathDateYear int,
  deathDateMonth int,
  deathDateDay int,
  dead boolean,
  anamnesis longtext,
  
  index ix_patient_name(lastName),
  index ix_patient_dead(dead),
  index ix_patient_date_birth(birthDate),
  index ix_patient_date_death(deathDate),
  index ix_patient_date_diagnose(diagnoseDate) 
);

/*
alter table PatientData drop column birthDate;
alter table PatientData add column birthDateYear int;
alter table PatientData add column birthDateMonth int;
alter table PatientData add column birthDateDay int;

alter table PatientData drop column diagnoseDate;
alter table PatientData add column diagnosisDateYear int;
alter table PatientData add column diagnosisDateMonth int;
alter table PatientData add column diagnosisDateDay int;

alter table PatientData drop column deathDate;
alter table PatientData add column deathDateYear int;
alter table PatientData add column deathDateMonth int;
alter table PatientData add column deathDateDay int;

*/

create table MedicationToPatient (
  id int not null primary key auto_increment,
  patientId int,
  medicationId int,
  
  index ix_medicationtopatient_patientid(patientId),
  index ix_medicationtopatient_medicationid(medicationId),
  unique index ix_medicationtopatient_patientidmedicationid(patientId, medicationId)
);

create table ExaminationResults (
  id int not null primary key auto_increment,  
  patientId int not null,
  illnessId int not null,
  number int not null,
  matherial nvarchar(255),
  blood nvarchar(255),
  mielogramm nvarchar(255),
  treatmentDescription longtext,
  comments longtext,
  examinationDate date,
  examinationDateYear int,
  examinationDateMonth int,
  examinationDateDay int,
  illnessPhase nvarchar(255),
  typeName nvarchar(255) not null,

  index ix_examinationresults_patientid(patientId),  
  unique index ix_examinationresults_number(number),
  index ix_examinationresults_date(examinationDate),
  index ix_examinationresults_illnessphase(illnessPhase),
  index ix_examinationresults_typename(typeName)
);

/*
alter table ExaminationResults add column examinationDateYear int;
alter table ExaminationResults add column examinationDateMonth int;
alter table ExaminationResults add column examinationDateDay int;
*/

create table MedicationToExaminationResults (
  id int not null primary key auto_increment,
  examinationId int,
  medicationId int,
  
  index ix_medicationtoexamination_medicationid(medicationId),
  index ix_medicationtoexamination_examinationid(examinationId),
  unique index ix_medicationtoexaminationtopatient_medicationexaminationid(medicationId, examinationId)
);

create table ExaminationResultsToTag (
  id int not null primary key auto_increment,
  tagId int,
  examinationResultsId int,
  
  index ix_examinationtotag_tagid(tagId),
  index ix_examinationtotag_examinationid(examinationResultsId),
  unique index ix_examinationtotag_tagidexaminationid(tagId, examinationResultsId)
);

create table PatientToTag (
  id int not null primary key auto_increment,
  tagId int,
  patientId int,
  
  index ix_examinationtotag_tagid(tagId),
  index ix_examinationtotag_examinationid(patientId),
  unique index ix_examinationtotag_tagidexaminationid(tagId, patientId)
);

create table FishExamResults (
  id int not null primary key auto_increment,
  examinationResultsId int not null,
  nomenclaturalDescription nvarchar(255),
  comments longtext,
  
  index ix_fishresults_examinationid(examinationResultsId),
  index ix_fishresults_nomenclaturaldesc(nomenclaturalDescription)
);

create table CariotypeExamResults (
  id int not null primary key auto_increment,
  examinationResultsId int not null,
  nomenclaturalDescription nvarchar(255),
  comments longtext,
  
  index ix_cariotyperesults_examinationid(examinationResultsId),
  index ix_cariotyperesults_nomenclaturaldesc(nomenclaturalDescription)
);
