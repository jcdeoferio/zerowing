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

COPY patients (patientid, lastname, firstname, middlename, entity_id) FROM stdin;
\.


--
-- Data for Name: symptoms; Type: TABLE DATA; Schema: public; Owner: -
--

COPY symptoms (symptomid, symptom, entity_id) FROM stdin;
1	fever	913b08db-292e-4d61-ba05-3872ea4001bc
2	runny nose	24bbedef-095c-49bd-ac8c-2d0393e069ba
3	sore throat	3b51cdd3-4fda-4d88-8b37-2ea7d9254289
4	lethargy	059292d2-72d7-4573-b150-f17cb008ddb5
5	lack of appetite	77461242-9070-4ea0-9798-52b0a398ceba
6	coughing	44a1ae0a-7b9e-45e6-8318-9d79621cc959
7	nausea	9a713eea-091a-481f-b526-1a1b5634705b
8	vomiting	bf995cc6-57b9-4d7d-aaa3-06be20e00460
9	diarrhea	745575de-8232-44d5-8828-97b203bed15e
\.


--
-- Data for Name: patientsymptoms; Type: TABLE DATA; Schema: public; Owner: -
--

COPY patientsymptoms (patientid, symptomid, entity_id) FROM stdin;
\.


--
-- Data for Name: variables; Type: TABLE DATA; Schema: public; Owner: -
--

COPY variables (varname, value) FROM stdin;
clock	0
\.


--
-- PostgreSQL database dump complete
--

