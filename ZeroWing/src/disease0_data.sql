--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

--
-- Name: patients_patientid_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('patients_patientid_seq', 1, false);


--
-- Name: symptoms_symptomid_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('symptoms_symptomid_seq', 9, true);


--
-- Data for Name: patients; Type: TABLE DATA; Schema: public; Owner: -
--

COPY patients (patientid, lastname, firstname, middlename) FROM stdin;
\.


--
-- Data for Name: symptoms; Type: TABLE DATA; Schema: public; Owner: -
--

COPY symptoms (symptomid, symptom) FROM stdin;
1	fever
2	runny nose
3	sore throat
4	lethargy
5	lack of appetite
6	coughing
7	nausea
8	vomiting
9	diarrhea
\.


--
-- Data for Name: patientsymptoms; Type: TABLE DATA; Schema: public; Owner: -
--

COPY patientsymptoms (patientid, symptomid) FROM stdin;
\.


--
-- PostgreSQL database dump complete
--

