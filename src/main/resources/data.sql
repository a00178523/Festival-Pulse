-- =============================================
-- FESTIVALS
-- =============================================
INSERT INTO festival (id, name, description, start_date, end_date) VALUES
(1, 'Electric Picnic', 'Ireland''s premier arts and music festival held in Stradbally, Co. Laois', '2025-08-29', '2025-08-31'),
(2, 'Longitude', 'Urban music festival held in Marlay Park, Dublin', '2025-07-04', '2025-07-06'),
(3, 'Forbidden Fruit', 'Alternative music and arts festival in the Royal Hospital Kilmainham, Dublin', '2025-06-01', '2025-06-02'),
(4, 'Body & Soul', 'Boutique arts and music festival in Ballinlough Castle, Co. Westmeath', '2025-06-20', '2025-06-22');

-- =============================================
-- ELECTRIC PICNIC AREAS (festival_id = 1)
-- =============================================
INSERT INTO festival_area (festival_id, name, description, area_type, coordinates, base_color) VALUES
(1, 'Main Stage',         'The main outdoor stage headlined by top acts',         'Stage',         '[{"x":200,"y":150},{"x":350,"y":150},{"x":350,"y":280},{"x":200,"y":280}]', '#FF006E'),
(1, 'Electric Arena',     'Large indoor arena for electronic and dance acts',      'Stage',         '[{"x":400,"y":200},{"x":550,"y":200},{"x":550,"y":350},{"x":400,"y":350}]', '#8338EC'),
(1, 'Rankins Wood Stage', 'Intimate woodland stage for emerging artists',          'Stage',         '[{"x":150,"y":400},{"x":280,"y":400},{"x":280,"y":520},{"x":150,"y":520}]', '#06FFA5'),
(1, 'Food Village',       'Central food area with Irish and international cuisine','Food & Drink',   '[{"x":600,"y":250},{"x":750,"y":250},{"x":750,"y":380},{"x":600,"y":380}]', '#FFBE0B'),
(1, 'Trailer Park',       'Street food trucks and casual dining area',             'Food & Drink',   '[{"x":800,"y":300},{"x":920,"y":300},{"x":920,"y":400},{"x":800,"y":400}]', '#FF5400'),
(1, 'Craft Beer Garden',  'Local Irish craft beer and cider bar',                  'Food & Drink',   '[{"x":650,"y":450},{"x":780,"y":450},{"x":780,"y":550},{"x":650,"y":550}]', '#00F5FF'),
(1, 'Body & Mind',        'Wellness, yoga and mindfulness area',                   'Wellness',       '[{"x":350,"y":500},{"x":480,"y":500},{"x":480,"y":600},{"x":350,"y":600}]', '#B5179E'),
(1, 'First Aid Point',    'Main medical and first aid station',                    'Medical',        '[{"x":500,"y":100},{"x":600,"y":100},{"x":600,"y":180},{"x":500,"y":180}]', '#FF006E'),
(1, 'Campsite A',         'Main family campsite near the entrance',                'Campsite',       '[{"x":100,"y":600},{"x":250,"y":600},{"x":250,"y":720},{"x":100,"y":720}]', '#00D9FF'),
(1, 'Campsite B',         'General campsite on the east side of the grounds',      'Campsite',       '[{"x":850,"y":500},{"x":1000,"y":500},{"x":1000,"y":650},{"x":850,"y":650}]', '#FFD60A');

-- =============================================
-- LONGITUDE AREAS (festival_id = 2)
-- =============================================
INSERT INTO festival_area (festival_id, name, description, area_type, coordinates, base_color) VALUES
(2, 'Main Stage',         'Main outdoor stage in Marlay Park',                    'Stage',         '[{"x":250,"y":200},{"x":400,"y":200},{"x":400,"y":320},{"x":250,"y":320}]', '#FF006E'),
(2, 'Second Stage',       'Secondary stage for supporting acts',                  'Stage',         '[{"x":450,"y":250},{"x":580,"y":250},{"x":580,"y":360},{"x":450,"y":360}]', '#8338EC'),
(2, 'Dance Tent',         'Covered tent for DJ sets and electronic acts',         'Stage',         '[{"x":200,"y":400},{"x":350,"y":400},{"x":350,"y":520},{"x":200,"y":520}]', '#06FFA5'),
(2, 'Food Court',         'Central food and drink area',                          'Food & Drink',   '[{"x":600,"y":300},{"x":750,"y":300},{"x":750,"y":420},{"x":600,"y":420}]', '#FFBE0B'),
(2, 'Bar Village',        'Main bar area with multiple vendors',                  'Food & Drink',   '[{"x":400,"y":450},{"x":550,"y":450},{"x":550,"y":560},{"x":400,"y":560}]', '#00F5FF'),
(2, 'First Aid Point',    'Medical and first aid station',                        'Medical',        '[{"x":650,"y":150},{"x":750,"y":150},{"x":750,"y":230},{"x":650,"y":230}]', '#FF5400'),
(2, 'Main Entrance',      'Primary festival entrance and ticketing',              'Entrance',       '[{"x":100,"y":250},{"x":200,"y":250},{"x":200,"y":350},{"x":100,"y":350}]', '#B5179E');

-- =============================================
-- FORBIDDEN FRUIT AREAS (festival_id = 3)
-- =============================================
INSERT INTO festival_area (festival_id, name, description, area_type, coordinates, base_color) VALUES
(3, 'Main Stage',         'Outdoor main stage at RHK grounds',                   'Stage',         '[{"x":300,"y":180},{"x":450,"y":180},{"x":450,"y":300},{"x":300,"y":300}]', '#FF006E'),
(3, 'The Courtyard Stage','Intimate courtyard performance area',                  'Stage',         '[{"x":500,"y":220},{"x":620,"y":220},{"x":620,"y":330},{"x":500,"y":330}]', '#8338EC'),
(3, 'The Greenhouse',     'Indoor stage for electronic and alternative acts',     'Stage',         '[{"x":250,"y":380},{"x":380,"y":380},{"x":380,"y":490},{"x":250,"y":490}]', '#06FFA5'),
(3, 'Street Food Market', 'Artisan food stalls and street food vendors',          'Food & Drink',   '[{"x":650,"y":280},{"x":780,"y":280},{"x":780,"y":390},{"x":650,"y":390}]', '#FFBE0B'),
(3, 'Craft Bar',          'Craft beer and cocktail bar',                          'Food & Drink',   '[{"x":450,"y":420},{"x":580,"y":420},{"x":580,"y":520},{"x":450,"y":520}]', '#00F5FF'),
(3, 'First Aid Point',    'Medical and first aid station',                        'Medical',        '[{"x":700,"y":150},{"x":800,"y":150},{"x":800,"y":230},{"x":700,"y":230}]', '#FF5400');

-- =============================================
-- BODY & SOUL AREAS (festival_id = 4)
-- =============================================
INSERT INTO festival_area (festival_id, name, description, area_type, coordinates, base_color) VALUES
(4, 'Woodland Stage',     'Main stage set within the castle woodland',            'Stage',         '[{"x":280,"y":220},{"x":420,"y":220},{"x":420,"y":340},{"x":280,"y":340}]', '#FF006E'),
(4, 'Castle Stage',       'Stage set against the backdrop of Ballinlough Castle', 'Stage',         '[{"x":470,"y":180},{"x":600,"y":180},{"x":600,"y":290},{"x":470,"y":290}]', '#8338EC'),
(4, 'The Sanctuary',      'Wellness and healing arts area',                       'Wellness',       '[{"x":650,"y":250},{"x":780,"y":250},{"x":780,"y":360},{"x":650,"y":360}]', '#B5179E'),
(4, 'The Lakeside',       'Lakeside bar and chill-out area',                      'Food & Drink',   '[{"x":300,"y":400},{"x":450,"y":400},{"x":450,"y":510},{"x":300,"y":510}]', '#00F5FF'),
(4, 'Food Forest',        'Organic and artisan food village',                     'Food & Drink',   '[{"x":500,"y":380},{"x":650,"y":380},{"x":650,"y":490},{"x":500,"y":490}]', '#FFBE0B'),
(4, 'First Aid Point',    'Medical and first aid station',                        'Medical',        '[{"x":700,"y":150},{"x":800,"y":150},{"x":800,"y":230},{"x":700,"y":230}]', '#FF5400'),
(4, 'Campsite',           'Main campsite on the castle grounds',                  'Campsite',       '[{"x":150,"y":550},{"x":350,"y":550},{"x":350,"y":680},{"x":150,"y":680}]', '#00D9FF');
