update athletes set category =
    CASE
        WHEN date_part('year', now()) - date_part('year', birth_date) <= 5 THEN 'chupetin'
        WHEN date_part('year', now()) - date_part('year', birth_date) <= 7 THEN 'sub8'
        WHEN date_part('year', now()) - date_part('year', birth_date) <= 9 THEN 'sub10'
        WHEN date_part('year', now()) - date_part('year', birth_date) <= 11 THEN 'sub12'
        WHEN date_part('year', now()) - date_part('year', birth_date) <= 13 THEN 'sub14'
        WHEN date_part('year', now()) - date_part('year', birth_date) <= 15 THEN 'sub16'
        WHEN date_part('year', now()) - date_part('year', birth_date) <= 17 THEN 'sub18'
        WHEN date_part('year', now()) - date_part('year', birth_date) <= 19 THEN 'sub20'
        WHEN date_part('year', now()) - date_part('year', birth_date) <= 22 THEN 'sub23'
        WHEN date_part('year', now()) - date_part('year', birth_date) <= 34 THEN 'senior'
        ELSE 'master'
    END
    
    update athletes set dorsal_category =
    CASE
        WHEN gender = 'female' and category = 'sub8' THEN 'u8f'
        WHEN gender = 'female' and category = 'sub10' THEN 'u10f'
        WHEN gender = 'female' and category = 'sub12' THEN 'u12f'
        WHEN gender = 'female' and category = 'sub14' THEN 'u14f'
        WHEN gender = 'female' and category ='sub16' THEN 'u16f'
        WHEN gender = 'female' and category = 'sub18' THEN 'u18f'
        WHEN gender = 'female' and category = 'sub20' THEN 'u20f'
        WHEN gender = 'female' and category = 'sub23' THEN 'u23f'
        WHEN gender = 'female' and category = 'senior' THEN 'sf'
        WHEN gender = 'female' and category = 'master' THEN 'mf'
        WHEN gender = 'male' and category = 'sub8' THEN 'u8m'
        WHEN gender = 'male' and category = 'sub10' THEN 'u10m'
        WHEN gender = 'male' and category = 'sub12' THEN 'u12m'
        WHEN gender = 'male' and category = 'sub14' THEN 'u14m'
        WHEN gender = 'male' and category ='sub16' THEN 'u16m'
        WHEN gender = 'male' and category = 'sub18' THEN 'u18m'
        WHEN gender = 'male' and category = 'sub20' THEN 'u20m'
        WHEN gender = 'male' and category = 'sub23' THEN 'u23m'
        WHEN gender = 'male' and category = 'senior' THEN 'sm'
        WHEN gender = 'male' and category = 'master' THEN 'mm'
    END
WHERE category is not null and gender is not null;