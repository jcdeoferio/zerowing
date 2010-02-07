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
200603113	studentid	200603113
200603113	lastname	MERCADO
200603113	firstname	GLADYS ANNE
200603113	middlename	MOJARES
200603113	birthdate	1990-06-12
200603113	gender	F
200603113	mobilephone	09194660526
200603113	email	trowa_1500@yahoo.com
200778807	studentid	200778807
200778807	lastname	UY
200778807	firstname	BONN CHERCHEZ
200778807	middlename	MONTEZA
200778807	birthdate	1983-06-15
200778807	gender	M
200778807	mobilephone	09278620224
200778807	email	bonncherchez.uy@up.edu.ph
200701212	studentid	200701212
200701212	lastname	STA ANA
200701212	firstname	CHRISTIAN
200701212	middlename	SISON
200701212	birthdate	1990-10-19
200701212	gender	M
200701212	mobilephone	null
200701212	email	kingsword11@yahoo.com
\.


--
-- Data for Name: updates; Type: TABLE DATA; Schema: public; Owner: -
--

COPY updates (peername, "timestamp", operation, tablename, entity, attribute, value) FROM stdin;
B	0	I	students	200603113	studentid	200603113
B	1	I	students	200603113	lastname	MERCADO
B	2	I	students	200603113	firstname	GLADYS ANNE
B	3	I	students	200603113	middlename	MOJARES
B	4	I	students	200603113	birthdate	1990-06-12
B	5	I	students	200603113	gender	F
B	6	I	students	200603113	mobilephone	09194660526
B	7	I	students	200603113	email	trowa_1500@yahoo.com
B	8	I	students	200778807	studentid	200778807
B	9	I	students	200778807	lastname	UY
B	10	I	students	200778807	firstname	BONN CHERCHEZ
B	11	I	students	200778807	middlename	MONTEZA
B	12	I	students	200778807	birthdate	1983-06-15
B	13	I	students	200778807	gender	M
B	14	I	students	200778807	mobilephone	09278620224
B	15	I	students	200778807	email	bonncherchez.uy@up.edu.ph
B	16	I	students	200701212	studentid	200701212
B	17	I	students	200701212	lastname	STA ANA
B	18	I	students	200701212	firstname	CHRISTIAN
B	19	I	students	200701212	middlename	SISON
B	20	I	students	200701212	birthdate	1990-10-19
B	21	I	students	200701212	gender	M
B	22	I	students	200701212	mobilephone	null
B	23	I	students	200701212	email	kingsword11@yahoo.com
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

