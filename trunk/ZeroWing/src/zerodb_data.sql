--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

--
-- Data for Name: students; Type: TABLE DATA; Schema: public; Owner: -
--

COPY students (entity, attribute, value) FROM stdin;
200746415	studentid	200746415
200746415	lastname	EDUALINO
200746415	firstname	JEAN CLAUDE
200746415	middlename	RONOLO
200746415	birthdate	1990-10-16
200746415	gender	M
200746415	mobilephone	639215439281
200746415	email	cedualino200@yahoo.com
200701323	studentid	200701323
200701323	lastname	DEOFERIO
200701323	firstname	JUAN CARLO
200701323	middlename	UYTIEPO
200701323	birthdate	1991-01-13
200701323	gender	M
200701323	mobilephone	09164041479
200701323	email	jc.deoferio@gmail.com
200618708	studentid	200618708
200618708	lastname	LORENZANA
200618708	firstname	RICHARD KEVIN
200618708	middlename	CABIO
200618708	birthdate	1988-05-13
200618708	gender	M
200618708	mobilephone	0927-518-1215
200618708	email	kevinzana@yahoo.com
\.


--
-- Data for Name: updates; Type: TABLE DATA; Schema: public; Owner: -
--

COPY updates (peername, "timestamp", operation, tablename, entity, attribute, value) FROM stdin;
A	0	I	students	200746415	studentid	200746415
A	1	I	students	200746415	lastname	EDUALINO
A	2	I	students	200746415	firstname	JEAN CLAUDE
A	3	I	students	200746415	middlename	RONOLO
A	4	I	students	200746415	birthdate	1990-10-16
A	5	I	students	200746415	gender	M
A	6	I	students	200746415	mobilephone	639215439281
A	7	I	students	200746415	email	cedualino200@yahoo.com
A	8	I	students	200701323	studentid	200701323
A	9	I	students	200701323	lastname	DEOFERIO
A	10	I	students	200701323	firstname	JUAN CARLO
A	11	I	students	200701323	middlename	UYTIEPO
A	12	I	students	200701323	birthdate	1991-01-13
A	13	I	students	200701323	gender	M
A	14	I	students	200701323	mobilephone	09164041479
A	15	I	students	200701323	email	jc.deoferio@gmail.com
A	16	I	students	200618708	studentid	200618708
A	17	I	students	200618708	lastname	LORENZANA
A	18	I	students	200618708	firstname	RICHARD KEVIN
A	19	I	students	200618708	middlename	CABIO
A	20	I	students	200618708	birthdate	1988-05-13
A	21	I	students	200618708	gender	M
A	22	I	students	200618708	mobilephone	0927-518-1215
A	23	I	students	200618708	email	kevinzana@yahoo.com
\.


--
-- Data for Name: variables; Type: TABLE DATA; Schema: public; Owner: -
--

COPY variables (varname, value) FROM stdin;
clock	24
\.


--
-- PostgreSQL database dump complete
--

