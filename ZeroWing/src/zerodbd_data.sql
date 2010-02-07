--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: classes; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE classes (
    entity integer,
    attribute character varying(20),
    value text
);


--
-- Name: updates; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE updates (
    peername character varying(20),
    "timestamp" integer,
    operation character(1),
    tablename character varying(20),
    entity integer,
    attribute character varying(20),
    value text
);


--
-- Name: variables; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE variables (
    varname character varying(20),
    value text
);


--
-- Data for Name: classes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY classes (entity, attribute, value) FROM stdin;
1	class	CS 131
1	instrutors	QUIWA, EVANGEL P.
1	slots	30
1	enlisted	28
2	class	CS 131
2	instrutors	TBA
2	slots	30
2	enlisted	0
1	unit	DCS
2	unit	DCS
\.


--
-- Data for Name: updates; Type: TABLE DATA; Schema: public; Owner: -
--

COPY updates (peername, "timestamp", operation, tablename, entity, attribute, value) FROM stdin;
D	0	I	classes	1	class	CS 131
D	1	I	classes	1	instructors	QUIWA, EVANGEL P.
D	2	I	classes	1	slots	30
D	3	I	classes	1	enlisted	28
D	4	I	classes	2	class	CS 131
D	5	I	classes	2	instructors	TBA
D	6	I	classes	2	slots	30
D	7	I	classes	2	enlisted	0
D	8	I	classes	1	unit	DCS
D	9	I	classes	2	unit	DCS
\.


--
-- Data for Name: variables; Type: TABLE DATA; Schema: public; Owner: -
--

COPY variables (varname, value) FROM stdin;
clock	10
\.


--
-- PostgreSQL database dump complete
--

