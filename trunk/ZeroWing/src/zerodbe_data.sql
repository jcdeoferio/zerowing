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
3	class	ECE 141
3	instructors	MARCIANO, JOEL JOSEPH JR.
3	slots	20
3	enlisted	19
4	class	ECE 141
4	instructors	MARCIANO, JOEL JOSEPH JR. / TAI, TYRONE
4	slots	50
4	enlisted	48
4	unit	DEEE
3	unit	DEEE
\.


--
-- Data for Name: updates; Type: TABLE DATA; Schema: public; Owner: -
--

COPY updates (peername, "timestamp", operation, tablename, entity, attribute, value) FROM stdin;
E	0	I	classes	3	class	ECE 141
E	1	I	classes	3	instructors	MARCIANO, JOEL JOSEPH JR.
E	2	I	classes	3	slots	20
E	3	I	classes	3	enlisted	19
E	4	I	classes	4	class	ECE 141
E	5	I	classes	4	instructors	MARCIANO, JOEL JOSEPH JR. / TAI, TYRONE
E	6	I	classes	4	slots	50
E	7	I	classes	4	enlisted	48
E	8	I	classes	4	unit	DEEE
E	9	I	classes	3	unit	DEEE
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

