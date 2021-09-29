select * from player p ;
select * from pair_of_cards poc ;
select s.seat_id_poc , * from seat s where s.seat_card1 is not null;
select s.seat_id_poc , * from seat s where s.seat_id_poc = 'AA';


select count(*) from tournaments t ;

delete from tournaments ;
delete from players ;

select * from players p ;

select * from tournaments t ;
select count(*) from players p ;
select * from players;
select * from tournaments t where t.tournament_id = '3067368972';
delete from tournaments t where t.tournament_id = '3067368972';

select * from cards c where player = 'jcarlos.vale';
select * from seats s ;

select count(*) from pokerline p2 where p2."section" = 'HEADER' ;
select * from pokerfile p ;
select * from hands h where h.tournament_id = '3067368972' order by played_at desc limit 1;

select * from hands h;

select poker_file_id from pokerfile where is_processed = false;
select * from pokerfile p2 ;
select * from pokerline p where section = 'HEADER';
select count(*), "section" from pokerline p group by "section" ;

select count(*) from pokerline p where p.line like '%PokerStars Hand #%';
select count() from pokerline p where p.line like '%* SUMMARY **%';
select count(*) from pokerline p;
select poker_file_id from pokerfile where is_processed = false;
select * from pokerfile p ;


select distinct regexp_matches(line, 'Tournament #([0-9]+)') 
from pokerline where 
line like '%Tournament #%'
and section = 'HEADER';


INSERT INTO players
(select  
distinct trim(substring(line, position(':' in line) + 1, length(line) - position(':' in line) - position('(' in reverse(line)))), --last position
now()
from pokerline pl
join pokerfile pf on (pl.poker_file_id = pf.poker_file_id)
where 
pf.is_processed = false 
and line like '%Seat %:%in chips)'
and section = 'HEADER')
ON CONFLICT (nickname)
do nothing;


select  
distinct trim(substring(line, position(':' in line) + 1, length(line) - position(':' in line) - position('(' in reverse(line)))), --last position
now()
from pokerline where 
line like '%Seat %:%in chips)'
and section = 'HEADER'; 
and poker_file_id = 11019;

select count(*) from players p;

delete from players;

select count(*) from seats s ;
select * from cards c;

select 
distinct (regexp_matches(line, 'Tournament #([0-9]+)'))[1],
now(),
pf.file_name 
from pokerline pl
join pokerfile pf on (pl.poker_file_id = pf.poker_file_id)
where 
pf.is_processed = false 
and line like '%Tournament #%'
and section = 'HEADER';


INSERT INTO tournaments
select 
distinct regexp_matches(line, 'Tournament #([0-9]+)'),
now(),
pf.file_name 
from pokerline pl
join pokerfile pf on (pl.poker_file_id = pf.poker_file_id)
where 
pf.is_processed = false 
and line like '%Tournament #%'
and section = 'HEADER'


select * from tournaments t ;

delete from tournaments;


INSERT INTO hands
(hand_id, created_at, played_at, tournament_id)
(select LINE,
(regexp_matches(line, 'PokerStars Hand #([0-9]+)'))[1],
now(),
to_timestamp((regexp_matches(line, '[0-9]{4}/[0-9]{1,2}/[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}'))[1], 'YYYY/MM/DD HH24:MI:SS'),
(regexp_matches(line, 'Tournament #([0-9]+)'))[1]
from pokerline pl
join pokerfile pf on (pl.poker_file_id = pf.poker_file_id)
where 
pf.is_processed = false 
and line like '%PokerStars Hand #%'
and section = 'HEADER')
on conflict (hand_id)
do nothing;

select COUNT(*) from hands h;


select h.hand_id from hands h
join tournaments t on (h.tournament_id = t.tournament_id)
where 
t.tournament_id = '3220560168';

INSERT INTO public.hands
VALUES('', '', '', '');

select
trim(substring(line, 1, position(':' in line)-1)) as player,
trim(substring(line, position(': shows [' in line)+9, 5)) as cards
from pokerline pl
where line like ('%: shows [%') and "section" = 'SHOWDOWN';

select
case 
	when position(' (small blind)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 24))
	when position(' (big blind)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 22))
    when position(' (button)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 19))
    else trim(substring(line, position(': ' in line)+2, position('mucked' in line) - position(':' in line) - 3))
end as player,
trim(substring(line, position('mucked [' in line)+8, 5)) as cards
from pokerline pl
where line like ('%mucked [%') and "section" = 'SUMMARY';


select count(*) from seats s ;

select * from players p where p.nickname like 'paulo%';



select  
distinct trim(substring(line, position(':' in line) + 1, length(line) - position(':' in line) - position('(' in reverse(line)))), --last position
now()
from pokerline pl
join pokerfile pf on (pl.poker_file_id = pf.poker_file_id)
where 
pf.is_processed = false 
and line like '%Seat %:%in chips)'
and section = 'HEADER'
and pf.poker_file_id = 105132;

select * from pokerline where line like '%Nice_play_%' and section = 'HEADER'; 
select * from pokerline where section = 'HEADER' and poker_file_id = 105132;
select * from pokerfile where poker_file_id = 105132;

select  
line, 
trim(substring(line, position(':' in line) + 1, length(line) - position(':' in line) - position('(' in reverse(line))))
from pokerline pl
where 
line like '%Seat %:%in chips%'
and section = 'HEADER'
and poker_file_id = 105215 
and line_number = 6;


select * from seats s ;

select * from pokerline pl
where 
line like ('%jcarlos.vale (button)%: shows [%') or line like ('%jcarlos.vale (button)%mucked [%');


select
trim(substring(line, 1, position(': shows [' in line)-1)) as player,
trim(substring(line, position(': shows [' in line)+9, 5)) as cards
from pokerline 
where 
line_number = 3301 and poker_file_id = 105306;

select * from pokerline where line like '(jcarlos.vale (button)'

select
line,
case 
    when position(' (button) (small blind)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 33))
	when position(' (small blind)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 24))
	when position(' (big blind)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 22))
	when position(' (button)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 19))
    else trim(substring(line, position(': ' in line)+2, position('mucked' in line) - position(':' in line) - 3))
end as player,
trim(substring(line, position('mucked [' in line)+8, 5)) as cards
from pokerline 
where line_number = 3275 and poker_file_id = 105132;

select * from pokerline p2 where hand_id = '220436247582';


select  
trim(substring(line, 1, position(': shows [' in line)-1)) as player,  
trim(substring(line, position(': shows [' in line)+9, 5)) as cards,  
trim(hand_id) as hand  
from pokerline   
where line like ('%: shows [%') and section = 'SHOWDOWN';

select  
case  
when position(' (button) (small blind)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 33))  
when position(' (small blind)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 24))  
when position(' (big blind)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 22))  
when position(' (button)' in line) > 0 then trim(substring(line, 9, position('mucked' in line) - 19))  
else trim(substring(line, position(': ' in line)+2, position('mucked' in line) - position(':' in line) - 3))  
end as player,  
trim(substring(line, position('mucked [' in line)+8, 5)) as cards,  
trim(hand_id) as hand  
from pokerline pl  
where line like ('%mucked [%') and section = 'SUMMARY' and hand_id is null;

select count(*), hand_id from seats group by hand_id ;
select count(*) from pokerline;


select * from pokerline where line like ('%mucked [%') and section = 'SUMMARY' limit 10;

select line,
split_part(line, ' ', 3) 
from pokerline where section = 'SUMMARY' and line like '%Seat %:%';

select * from pokerline where hand_id = '223466809761';

select * from pokerline p where p."section" = 'SUMMARY' and line like '%Seat %:%';

select 
	line,
	split_part(line, ' ', 3) as player,
	hand_id as hand
from pokerline
where  hand_id = '221918338318' and "section" = 'SUMMARY';	
		
select * from pokerline where hand_id = '3061616310';
select * from pokerfile p ;

select * from pokerline pl
join pokerfile pf on pl.poker_file_id = pf.poker_file_id 
join tournaments t on t.file_name = pf.file_name 
where t.tournament_id = '3061616310';

select 
	trim(substring(line, position(':' in line) + 1, length(line) - position(':' in line) - position('(' in reverse(line)))) as player,
	trim(substring(line, 6,1)) as position,
	hand_id as hand
from pokerline pl
join pokerfile pf on (pl.poker_file_id = pf.poker_file_id)
where
	pf.is_processed = false
	and section = 'HEADER' 
	and line like '%Seat %:%in chips%';	


select 
	line,
	trim(substring(line, 6,1)) as position,
	case 
		when position('mucked [' in line) > 0 then trim(substring(line, position('mucked [' in line)+8, 5))
		when position('showed [' in line) > 0 then trim(substring(line, position('showed [' in line)+8, 5))
		else null
	end as cards,
	hand_id as hand
from pokerline pl
where
	 section = 'SUMMARY' 
	and line like '%Seat %:%'
	and tournament_id = 3235489658;

select count(*) from seats s ;

UPDATE public.pokerfile SET is_processed=true;

select * from hands h ;

select count(distinct hand_id) from pokerline;

select * from pokerline p where tournament_id = 3235489658 order by line_number ;


select * from pokerline p2 where tournament_id = 3055967339;



select 
(regexp_matches(line, 'PokerStars Hand #([0-9]+)'))[1] as handId, 
now() as createdAt, 
to_timestamp((regexp_matches(line, '[0-9]{4}/[0-9]{1,2}/[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}'))[1], 'YYYY/MM/DD HH24:MI:SS') as playedAt, 
(regexp_matches(line, 'Tournament #([0-9]+)'))[1] as tournamentId
from pokerline
where  
is_processed = false  
and line like '%PokerStars Hand #%'
and section = 'HEADER';


INSERT INTO hands 
(hand_id, created_at, played_at, tournament_id) 
(select 
cast((regexp_matches(line, 'PokerStars Hand #([0-9]+)'))[1] as int8), 
now(), 
to_timestamp((regexp_matches(line, '[0-9]{4}/[0-9]{1,2}/[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}'))[1], 'YYYY/MM/DD HH24:MI:SS'), 
cast((regexp_matches(line, 'Tournament #([0-9]+)'))[1] as int8) 
from pokerline 
where  
is_processed = false  
and line like '%PokerStars Hand #%' 
and section = 'HEADER')
on conflict (hand_id) 
do nothing; 


update pokerline set is_processed = false;

select * from pokerline p where p.is_processed = true;

select * from seats s ;

select 
trim(substring(line, 5, position(':' in line) - 5)) as position, 
case 
   when position('mucked [' in line) > 0 then trim(substring(line, position('mucked [' in line)+8, 5))
   when position('showed [' in line) > 0 then trim(substring(line, position('showed [' in line)+8, 5))
   else null 
end as cards, 
   hand_id as hand 
from pokerline 
where 
   is_processed = false 
   and section = 'SUMMARY' 
   and line like '%Seat %:%';
   
  
select 
trim(substring(line, position(':' in line) + 1, length(line) - position(':' in line) - position('(' in reverse(line)))) as player, 
trim(substring(line, 6,1)) as position, 
hand_id as hand 
from pokerline 
where 
   is_processed = false 
   and section = 'HEADER' 
   and line like '%Seat %:%in chips%';  
  
  
  select * from cards order by normalised ;
   
  
  
select 
trim(substring(line, 5, position(':' in line) - 5)) as position, 
case 
   when position('mucked [' in line) > 0 then trim(substring(line, position('mucked [' in line)+8, 5))
   when position('showed [' in line) > 0 then trim(substring(line, position('showed [' in line)+8, 5))
   else null 
end as cards, 
   hand_id as hand 
from pokerline 
where 
   is_processed = false 
   and section = 'SUMMARY' 
   and line like '%Seat %:%'  
   
select 
trim(substring(line, 5, position(':' in line) - 5)) as position, 
case 
   when position('mucked [' in line) > 0 then trim(substring(line, position('mucked [' in line)+8, 5))
   when position('showed [' in line) > 0 then trim(substring(line, position('showed [' in line)+8, 5))
   else null 
end as cards, 
   hand_id as hand 
from pokerline 
where 
   is_processed = false 
   and section = 'SUMMARY' 
   and line like '%Seat %:%' and (line like '%mucked [%' or line like '%showed [%');
   
  
  INSERT INTO player_position 
(nickname, position, hand_id) 
(select 
trim(substring(line, position(':' in line) + 1, length(line) - position(':' in line) - position('(' in reverse(line)))) as player, 
cast(trim(substring(line, 6,1)) as int8) as position, 
hand_id as hand 
from pokerline 
where 
   is_processed = false 
   and section = 'HEADER' 
   and line like '%Seat %:%in chips%')
on conflict (hand_id, nickname, position) 
do nothing 

select * from player_position pp ;
select * from cards_of_player cop;


INSERT INTO cards_of_player 
(position, cards, hand_id) 
(select 
cast(trim(substring(line, 5, position(':' in line) - 5)) as int8) as position, 
case 
   when position('mucked [' in line) > 0 then trim(substring(line, position('mucked [' in line)+8, 5)) 
   when position('showed [' in line) > 0 then trim(substring(line, position('showed [' in line)+8, 5)) 
   else null 
end as cards, 
   hand_id as hand 
from pokerline 
where 
   is_processed = false 
   and section = 'SUMMARY' 
   and line like '%Seat %:%' and (line like '%mucked [%' or line like '%showed [%'))
on conflict (hand_id, cards, position) 
do nothing 


select count(*) 
from player_position pp
where pp.nickname = 'jcarlos.vale';


select * 
from cards_of_player cop 
join player_position pp on cop.hand_id = pp.hand_id 
where pp.nickname = 'maxbornholdt';


(select 
cast(trim(substring(line, 5, position(':' in line) - 5)) as int8) as position, 
case 
   when position('mucked [' in line) > 0 then trim(substring(line, position('mucked [' in line)+8, 5)) 
   when position('showed [' in line) > 0 then trim(substring(line, position('showed [' in line)+8, 5)) 
   else null 
end as cards, 
   hand_id as hand 
from pokerline 
where 
--   is_processed = false 
   tournament_id = 3272803091
   and section = 'SUMMARY' 
   and line like '%Seat %:%' and (line like '%mucked [%' or line like '%showed [%'));

  
  (length(line) - strpos(reverse(line),'(')+1) as lastParenthesis,
  
select 
substring(line from '(\([0-9]* in chips\))') as regex1,
substring(line from 'Seat [0-9]*:') as regex2,
substring(line from 'Seat ([0-9]*):') as position,
substring(line from 'Seat [0-9]*:(.*)\([0-9]* in chips\)') as player,
substring(line from '\(([0-9]*) in chips\)') as stack,
line
--regexp_split_to_array(line,'(\([0-9]* in chips\))')[1] as teste 
--substring(line from ':[ ].* ')
from pokerline 
where 
   --tournament_id = 3082657132
   section = 'HEADER' 
   and line like '%Seat %:%in chips%';
   --and line_number = 334
--   order by line_number;

(select 
line,substring(line from 'Seat ([0-9]*):') as position, case    when position('mucked [' in line) > 0 then substring(line from 'mucked \[(.{5})\]')    when position('showed [' in line) > 0 then substring(line from 'showed \[(.{5})\]')    else null end as cards,    hand_id as hand from pokerline where    hand_id = 221899067017
   and section = 'SUMMARY'    and line like '%Seat %:%' and (line like '%mucked [%' or line like '%showed [%'));
   
  
  
  select 
  hand_id, 
  line,trim(substring(line from 'Seat [0-9]*:(.*)\([0-9]* in chips')) as nickname, cast(trim(substring(line from 'Seat ([0-9]*):')) as int8) as position, cast(trim(substring(line from '\(([0-9]*) in chips')) as int8) as stack  from pokerline where    section = 'HEADER'    and line like '%Seat %:%in chips%'
   and hand_id  = 221329997824
   and cast(trim(substring(line from '\(([0-9]*) in chips\)')) as int8) is null;
   
  
select count(*) from player_position pp 
union
select count(*) from hands h
union 
select count(*) from cards_of_player cop 


SELECT * FROM player_position pp where pp.hand_id = 221914407250;

select * from pokerline p 
where p.tournament_id = '3082657132' order by line_number ;


   select 
	   line,
	   trim(substring(line from 'Level(.*)\(')) as level,
	   cast(trim(substring(line from '\(([0-9]*)/')) as int8) as smallblind,
	   cast(trim(substring(line from '/([0-9]*)\)')) as int8) as bigblind,
       hand_id,        now(),        to_timestamp((regexp_matches(line, '[0-9]{4}/[0-9]{1,2}/[0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}'))[1], 'YYYY/MM/DD HH24:MI:SS'),        tournament_id    from pokerline    where     line like '%PokerStars Hand #%'    and section = 'HEADER'
   and tournament_id = '3082657132';   

