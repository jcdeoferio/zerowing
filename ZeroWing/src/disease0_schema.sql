CREATE TABLE patients (
    patientid integer NOT NULL,
    lastname character varying(32),
    firstname character varying(32),
    middlename character varying(32)
);


--
-- Name: cu_patients; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW cu_patients AS
    SELECT patients.entity_id AS cu_entity_id, patients.lastname, patients.firstname, patients.middlename FROM patients;


--
-- Name: symptoms; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE symptoms (
    symptomid integer NOT NULL,
    symptom character varying(64)
);


--
-- Name: cu_symptoms; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW cu_symptoms AS
    SELECT symptoms.entity_id AS cu_entity_id, symptoms.symptom FROM symptoms;


--
-- Name: patients_patientid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE patients_patientid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- Name: patients_patientid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE patients_patientid_seq OWNED BY patients.patientid;


--
-- Name: patientsymptoms; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE patientsymptoms (
    patientid integer NOT NULL,
    symptomid integer NOT NULL
);


--
-- Name: symptoms_symptomid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE symptoms_symptomid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- Name: symptoms_symptomid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE symptoms_symptomid_seq OWNED BY symptoms.symptomid;


--
-- Name: variables; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE variables (
    varname character varying(20),
    value text
);


--
-- Name: patientid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE patients ALTER COLUMN patientid SET DEFAULT nextval('patients_patientid_seq'::regclass);


--
-- Name: symptomid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE symptoms ALTER COLUMN symptomid SET DEFAULT nextval('symptoms_symptomid_seq'::regclass);


--
-- Name: patients_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY patients
    ADD CONSTRAINT patients_pkey PRIMARY KEY (patientid);


--
-- Name: patientsymptoms_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY patientsymptoms
    ADD CONSTRAINT patientsymptoms_pkey PRIMARY KEY (patientid, symptomid);


--
-- Name: symptoms_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY symptoms
    ADD CONSTRAINT symptoms_pkey PRIMARY KEY (symptomid);


--
-- Name: patientsymptoms_patientid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY patientsymptoms
    ADD CONSTRAINT patientsymptoms_patientid_fkey FOREIGN KEY (patientid) REFERENCES patients(patientid);


--
-- Name: patientsymptoms_symptomid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY patientsymptoms
    ADD CONSTRAINT patientsymptoms_symptomid_fkey FOREIGN KEY (symptomid) REFERENCES symptoms(symptomid);


--
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

