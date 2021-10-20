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


  
select 
	distinct trim(substring(line from 'Seat [0-9]*:(.*)\([0-9]* in chips')) as nickname,
	now()
from pokerline 
where 
   section = 'HEADER' 
   and line like '%Seat %:%in chips%';  

   select        cast(substring(line from 'Seat ([0-9]*):') as int8) as position,        case            when position('mucked [' in line) > 0 then substring(line from 'mucked \[(.{5})\]')            when position('showed [' in line) > 0 then substring(line from 'showed \[(.{5})\]')            else null        end as cards,        hand_id as hand    from pokerline    where        section = 'SUMMARY'        and line like '%Seat %:%'        and (line like '%mucked [%' or line like '%showed [%');
       

      
INSERT INTO cards_of_player (position, description, hand_id) (   select        cast(substring(line from 'Seat ([0-9]*):') as int8) as position,        case            when position('mucked [' in line) > 0 then substring(line from 'mucked \[(.{5})\]')            when position('showed [' in line) > 0 then substring(line from 'showed \[(.{5})\]')            else null        end as cards,        hand_id as hand    from pokerline    where        section = 'SUMMARY'        and line like '%Seat %:%'        and (line like '%mucked [%' or line like '%showed [%'))on conflict (hand_id, position) do nothing      


select * from player_position pp where hand_id = 220860170185;

select
   line,
   cast(substring(line from 'Seat ([0-9]*):') as int8) as position, 
   case 
       when position('mucked [' in line) > 0 then substring(line from 'mucked \[(.{5})\]') 
       when position('showed [' in line) > 0 then substring(line from 'showed \[(.{5})\]') 
       else null 
   end as cards, 
   hand_id as hand 
from pokerline 
where 
   section = 'SUMMARY' 
   and line like '%Seat %:%' 
   and (line like '%mucked [%' or line like '%showed [%')
   and hand_id is null;
  
  
select * from player_position pp where pp.hand_id :handId;	

select * from pokerline p 
where p."section" = 'SUMMARY' 
and p.line like '%pot%'or p.line like '%Board%';

select 
    hand_id,
    line,
    substring(line from 'Total pot ([0-9]*)')
from pokerline 
where 
	section = 'SUMMARY'
	and line like '%Total pot%';

select 
    hand_id,
    line,
    substring(line from 'Board \[(.*)\]')
from pokerline 
where 
	section = 'SUMMARY'
	and line like '%Board [%]%';

select count(*) from board_of_hand boh ;
select count(*) from pot_of_hand poh;
select count(*) from hands h ;


/*actions summary*/
select line, hand_id 
from pokerline p 
where 
	p."section" = 'SUMMARY'
	and p.line not like '%folded before Flop%' --nao betou
	and p.line not like '%folded on the Turn%' --fold turn
	and p.line not like '%folded on the Flop%' --fold flop
	and p.line not like '%folded on the River%' --fold river
	and p.line not like '%mucked%' --mucked
	and p.line not like '%collected%' --ganhou sem showdown
	and p.line not like '%and lost with%' --perdeu com showdown
	and p.line not like '%and won %' --ganhou com showdown
	and p.line not like '%*** SUMMARY ***%' --summary
	and p.line not like '%Total pot%' --pot
	and p.line not like '%Board%' --board
	;

select line, hand_id 
from pokerline p 
where 
	p."section" = 'SUMMARY'
	and p.line like '%folded before Flop%' --nao betou
	and p.line not like '%didn''t bet%'
;

select line 
from pokerline p 
where p.line like '%all-in%';
--p.line like '%folded%(didn''t bet)%'
	p.line like 'Seat%:%';


select line, hand_id 
from pokerline p 
where p.line like '%side pot%';
	p.line like 'Seat%:%';


select 
	line, 
	hand_id,
	cast(substring(line from 'Seat ([0-9]*):') as int8) as position,
	case    
    	when strpos(line, 'folded before Flop')  > 0 then 'PREFLOP'
    	when strpos(line, 'folded on the Flop')  > 0 then 'FLOP'
    	when strpos(line, 'folded on the Turn')  > 0 then 'TURN'
    	when strpos(line, 'folded on the River') > 0 then 'RIVER'    	
	end,
	case    
    	when strpos(line, 'didn''t bet')  > 0 then true
		else false
	end
from pokerline p 
where 
		p."section" = 'SUMMARY'
	and p.line like 'Seat%:%'
	and (p.line like '%folded before Flop%' 
	or  p.line like '%folded on the Flop%' 
	or  p.line like '%folded on the Turn%' 
	or  p.line like '%folded on the River%');


select 
	line, 
	hand_id,
	cast(substring(line from 'Seat ([0-9]*):') as int8) as position,
	case    
    	when strpos(line, 'collected') > 0 then false
    	when strpos(line, 'and won ' ) > 0 then true
	end	as showdown,
	case    
    	when strpos(line, 'collected') > 0 then cast(substring(line from 'collected \(([0-9]*)\)') as int8)
    	when strpos(line, 'and won ' ) > 0 then cast(substring(line from 'and won \(([0-9]*)\)')   as int8)
	end	as pot,
	case    
    	when strpos(line, 'collected') > 0 then null
    	when strpos(line, 'and won ' ) > 0 then trim(substring(line from '\) with (.*)'))
	end	as hand_description
from pokerline p 
where 
		p."section" = 'SUMMARY'
	and p.line like 'Seat%:%'
	and (p.line like '%collected%' 
	or  p.line like '%and won %');
	


select * from win_position wp where hand = 220856815323;

select h.hand_id 
from hands h
where h.tournament_id = 3082657132
order by h.hand_id asc;

select * from fold_position fp ;



select
   line,   hand_id,                                                                                                       cast(substring(line from 'Seat ([0-9]*):') as int8) as position,                                               trim(substring(line from 'and lost with (.*)')) as hand_description                                                                                     from pokerline p                                                                                               where                                                                                                             p.section = 'SUMMARY'                                                                                          and p.line like 'Seat%:%'                                                                                   and p.line like '%and lost %';   
  
  select * from pokerline p 
  where p."section" = 'SUMMARY'
  and p.line like 'Seat%:%'                                                                                   
  and p.line like '%and lost %';
   
 
 select * from lose_position order by hand;
 
select 
	line,
	p.hand_id as handId,
	p.tournament_id as tounamentId,
	trim(substring(line from 'Table ''[0-9]* (.*)''')) as table 
from pokerline p 
where p."section" = 'HEADER'
and p.line like '%Table%';

select * from pot_of_hand poh ;

   select        hand_id,        cast(substring(line from 'Total pot ([0-9]*)')  as int8)    from pokerline    where        section = 'SUMMARY'        and line like '%Total pot%';

      
      
select * from hands h 
join player_position pp on pp.hand_id = h.hand_id
left join fold_position fp on fp.hand = h.hand_id and fp."position" = pp."position" 
left join lose_position lp on lp.hand = h.hand_id and lp."position" = pp."position"
left join win_position  wp on wp.hand = h.hand_id and wp."position" = pp."position"
left join blind_position bp on bp.hand = h.hand_id and bp."position" = pp."position"
where h.tournament_id = 3082657132 
and pp.nickname = 'jcarlos.vale'
order by h.hand_id;

select 
	h.tournament_id,
	h.table_id,
	boh.board,
	h.hand_id,
	h.level,
 	h.small_blind,
	h.big_blind,
	poh.total_pot,
	pp.nickname,
	pp.position,
	bp.place,
	cop.description,	
	c.card1,
	c.card2,
	c.chen,
	c.normalised,
	c.pair,
	c.suited,
	pp.stack,
	fp.round as fold,
	fp.no_bet,
	lp.hand_description as loseHand,
	wp.hand_description as winHand,
	wp.pot as winPot,
	wp.showdown,
 	h.played_at 
from hands h
join player_position pp on pp.hand_id = h.hand_id
left join blind_position bp on bp.hand = pp.hand_id and bp.position = pp.position
left join board_of_hand boh on boh.hand_id = h.hand_id
left join cards_of_player cop on cop.hand = pp.hand_id and cop.position = pp.position
left join cards c on c.description = cop.description 
left join fold_position fp on fp.hand = pp.hand_id and fp.position = pp.position
left join lose_position lp on lp.hand = pp.hand_id and lp.position = pp.position
left join win_position wp on wp.hand = pp.hand_id and wp.position = pp.position
left join pot_of_hand poh on poh.hand_id = pp.hand_id
order by h.tournament_id , h.hand_id;



 select h.tournament_id, h.table_id,  boh.board,   h.hand_id,   h.level, h.small_blind,  h.big_blind, poh.total_pot,   pp.nickname, pp.position, bp.place,    cop.description, c.card1, c.card2, c.chen,  c.normalised,    c.pair,  c.suited,    pp.stack,    fp.round as fold,    fp.no_bet,   lp.hand_description as loseHand, wp.hand_description as winHand,  wp.pot as winPot,    wp.showdown, h.played_at     from hands h join player_position pp on pp.hand_id = h.hand_id    left join blind_position bp on bp.hand = pp.hand_id and bp.position = pp.position    left join board_of_hand boh on boh.hand_id = h.hand_id   left join cards_of_player cop on cop.hand = pp.hand_id and cop.position = pp.position    left join cards c on c.description = cop.description     left join fold_position fp on fp.hand = pp.hand_id and fp.position = pp.position left join lose_position lp on lp.hand = pp.hand_id and lp.position = pp.position left join win_position wp on wp.hand = pp.hand_id and wp.position = pp.position  left join pot_of_hand poh on poh.hand_id = pp.hand_id
where h.hand_id = 220856815323;

select * from hand_consolidation hc2 ;
select 
	hc.nickname as nickname,
	count(hc.hand) as totalHands,
	sum(case when cards_description is null then 0 else 1 end) as showdowns,
	round(sum(case when cards_description is null then 0 else 1 end) * 100.0/ count(hc.hand)) as showdownStat,
	round(avg(hc.chen)) as avgChen,
	min(hc.played_at) as createdAt,
	string_agg(distinct hc.normalised , ',') as cards,
	string_agg(hc.cards_description, ',') as rawcards,
	'd-none' as css
from hand_consolidation hc
where 
	hc.nickname = '$ekelfreddy$'
group by 
	hc.nickname;


select 
	t.tournament_id as tournamentId,
	t.file_name as fileName,
	min(hc.played_at) as createdAt,
	count(distinct hc.hand) as hands,
	count(distinct hc.nickname) as players,
	sum(case when hc.cards_description is null then 0 else 1 end) as showdowns
from tournaments t
join hand_consolidation hc on t.tournament_id = hc.tournament_id
where 
	t.tournament_id = 3060068759
group by 
	t.tournament_id,
	t.file_name,
	t.created_at ;

select 
	distinct hc.nickname as nickname,
	count(hc.hand) as totalHands,
	sum(case when cards_description is null then 0 else 1 end) as showdowns,
	round(sum(case when cards_description is null then 0 else 1 end) * 100.0/ count(hc.hand)) as showdownStat,
	round(avg(hc.chen)) as avgChen,
	min(hc.played_at) as createdAt,
	string_agg(distinct hc.normalised , ', ') as cards,
	string_agg(hc.cards_description, ', ') as rawcards,
	'd-none' as css
from hand_consolidation hc
group by 
	hc.nickname;
	
select 
	hc.tournament_id as tournamentId,
	hc.hand as handId,
	hc.level as level,
	concat(hc.small_blind::text || '/', hc.big_blind::text) as blinds,
	count(distinct hc.nickname) as players,
	sum(case when hc.cards_description is null then 0 else 1 end) as showdowns,
	to_char(hc.played_at, 'dd-mm-yy HH24:MI:SS')  as playedAt,
	hc.total_pot as pot,
	hc.board as board,
	case 
		when length(hc.board) = 8 then 'FLOP'
		when length(hc.board) = 11 then 'TURN'
		when length(hc.board) = 14 then 'RIVER'
		else null
	end as boardShowdown
from 
	hand_consolidation hc
where 
	hc.tournament_id = 3099770291
group by
	hc.tournament_id,
	hc.hand,
	hc.level,
	hc.small_blind,
	hc.big_blind,
	hc.played_at,
	hc.total_pot,
	hc.board
order by 
	hc.played_at;
	
select 
	t.tournament_id as tournamentId,
	t.file_name as fileName,
	to_char(min(hc.played_at), 'dd-mm-yy HH24:MI:SS') as playedAt,
	count(distinct hc.hand) as hands,
	count(distinct hc.nickname) as players,
	sum(case when hc.cards_description is null then 0 else 1 end) as showdowns
from tournaments t
join hand_consolidation hc on t.tournament_id = hc.tournament_id
group by 
	t.tournament_id,
	t.file_name;


select 
	hc.tournament_id,
	hc.hand,
	round(avg(hc.stack_of_player)) as avgStack
from 
	hand_consolidation hc 
where 
	hc.tournament_id = 3099770291
	and hc.hand = (select max(hand) from hand_consolidation)
group by
	hc.tournament_id,
	hc.hand;
	
select 
	hc.tournament_id,
	hc.hand,
	hc.nickname,
	hc.stack_of_player,
	hc.big_blind,
	round(hc.stack_of_player / hc.big_blind) as blinds,
	hc.total_pot as pot
from 
	hand_consolidation hc 
where 
	hc.tournament_id = 3099770291
	and hc.hand = (select max(hand) from hand_consolidation);
	
select
	hc.tournament_id as tournamentId,
	hc.hand as handId,
	hc.level as level,
	to_char(hc.played_at, 'dd-mm-yy HH24:MI:SS') as playedAt,
	case 
		when length(hc.board) = 8 then 'FLOP'
		when length(hc.board) = 11 then 'TURN'
		when length(hc.board) = 14 then 'RIVER'
		else null
	end as boardShowdown,
	concat(cast(hc.small_blind as text) || '/', cast(hc.big_blind as text)) as blinds,
	hc.board as board,
	hc.total_pot as pot,
	hc.nickname as nickname,
	hc.chen as chen,
	concat(cast(hc.normalised as text) || ' / ', cast(hc.cards_description as text)) as cards,
	case 
		when hc.place = 'button' then true
		else false
	end as isButton,
	case 
		when hc.place = 'small blind' then true
		else false
	end as isSmallBlind,
	case 
		when hc.place = 'big blind' then true
		else false
	end as isBigBlind,
	hc.stack_of_player as stackOfPlayer,
	round(hc.stack_of_player / hc.big_blind) as blindsCount,
	case 
		when hc.win_pot is null then false
		else true
	end as isWinner,
	case 
		when hc.lose_hand_description is not null then true
		else false
	end as isLose,
	case
		when hc.win_hand_description is not null then hc.win_hand_description
		else hc.lose_hand_description 
	end as handDescription,
	hc.place as place ,
	hc."position" as position
from hand_consolidation hc 
where 
hc.hand = 222697180735;

select * 
from hand_consolidation hc 
where 
hc.hand = 221588655360;

select 
	hc.hand,
	count(hc.*) as numberOfPlayers,
	min(hc.position) as minPos,
	max(hc.position) as maxPos,
	bp.position as button,
	string_agg(distinct cast(hc."position" as text) , ',') as positions
from 
	hand_consolidation hc 
left join 
	blind_position bp on hc.hand = bp.hand 
where 
 	bp.place = 'button'
group by
	hc.hand,
	bp.position;


select count(*) from hands h; 
select hc.nickname, hc."position" from hand_consolidation hc;
select distinct position from hand_consolidation hc order by position;



select h.hand_id 
from hands h
where h.hand_id not in 
(
select 
	hc.hand
from 
	hand_consolidation hc 
join 
	blind_position bp on hc.hand = bp.hand 
join 
	blind_position bp2 on hc.hand = bp2.hand 
where 
	bp.place = 'big blind'
	and bp2.place = 'button');

--221588655360

select * from pokerline p where p.hand_id = 221588655360;

select * from hand_position hp ;



select 
	hc.hand,
	count(hc.*) as numberOfPlayers,
	min(hc.position) as minPos,
	max(hc.position) as maxPos,
	bp.position as button,
	string_agg(distinct cast(hc."position" as text) , ',') as positions
from 
	hand_consolidation hc 
left join 
	blind_position bp on hc.hand = bp.hand 
where 
 	bp.place = 'button'
group by
	hc.hand,
	bp.position;
	

select * from hand_position hp;